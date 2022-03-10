package datademo;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: WenZhen.Jia
 * @Description:
 * @Date: Created in 10:00 2017/11/18
 */
public class CityBikeTest {

    // 默认单元格格式化日期字符串
    private static final SimpleDateFormat sdf = new SimpleDateFormat(  "yyyy-MM-dd HH:mm:ss");

    // 默认开始时间
    private String defaultStartTime = "2014-04-01 00:00:00";

    // 默认时间增加频率(单位：小时)，这里为默认增长1小时
    private int defaultTimeIncrement = 1;

    // 文件所在目录
    private static final String FILEPATH = "D:/HMTISC/Hierarchical_processing/Mobility_trend_dataprocessing/";

    // 文件导出目录
    private static final String EXPORTFILEPATH = "D:/HMTISC/Hierarchical_processing/Mobility_trend_dataprocessing//fandemo/";

    private static final Long TIME = 3600000l;

    private static final Map<String, String> clusterDataMap = new HashMap<String, String>();

    private Calendar calendar = Calendar.getInstance();

    private List<String> listNeedDel = new ArrayList<String>();

    private int ii = 0;

    public void testTime(){
        String timeEnd = null;
        try{
            for(int i = 0; i<3912; i ++){
                calendar.setTime(sdf.parse(defaultStartTime));
                calendar.add(Calendar.HOUR_OF_DAY, defaultTimeIncrement);
                timeEnd = sdf.format(calendar.getTime());
                System.out.println(defaultStartTime + " : " + timeEnd);
                defaultStartTime = timeEnd;
            }
        }catch(Exception error){

        }

    }

    /**
     * 读取一个csv表格的数据
     * @param fileName 文件名称
     * @return
     */
    public List<String> initData(String fileName){
        List<String> initList = new ArrayList<String>();
        try{
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(fileName);
            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()){
                // 读这行的某一列
                initList.add(csvReader.get("starttime") + "&&" + csvReader.get("start station latitude") + "&&"
                        + csvReader.get("start station longitude") + "&&" + csvReader.get("end station latitude") + "&&"
                        + csvReader.get("end station longitude"));
                //initList.add(csvReader.get("stoptime") + "&&" + csvReader.get("start station latitude") + "&&"
                        //+ csvReader.get("start station longitude") + "&&" + csvReader.get("end station latitude") + "&&"
                        //+ csvReader.get("end station longitude"));
            }
        }catch(Exception error){
            error.printStackTrace();
        }
        return initList;
    }

    /**
     * 汇总6个csv表格的数据
     * @return
     */
    public List<String> initDataSummary(){
        List<String> list = this.initData(FILEPATH + "NYCdata/2014-04 - Citi Bike trip data.csv");
        list.addAll(this.initData(FILEPATH + "NYCdata/2014-05 - Citi Bike trip data.csv"));
        list.addAll(this.initData(FILEPATH + "NYCdata/2014-06 - Citi Bike trip data.csv"));
        list.addAll(this.initData(FILEPATH + "NYCdata/2014-07 - Citi Bike trip data.csv"));
        list.addAll(this.initData(FILEPATH + "NYCdata/2014-08 - Citi Bike trip data.csv"));
        list.addAll(this.initData(FILEPATH + "NYCdata/2014-09 - Citi Bike trip data.csv"));
        return list;
    }

    public Map<Long,ArrayList<String>> initDataSummarySplit(){
        Map<Long,ArrayList<String>> map = new HashMap<Long,ArrayList<String>>();
        for(long i = 0; i < 3912; i++){
            map.put(i, new ArrayList<String>());
        }
        Date begin = null;
        try {
           begin = sdf.parse("2014-04-01 00:00:00");
        }catch(Exception error){
            error.printStackTrace();
        }
        Date end = null;
        List<String> list = this.initDataSummary();
        System.out.println("size: " + list.size());
        Iterator<String> iterator = list.iterator();
        String strTemp = "";
        String needDateFormater = "";
        int tt = 0;
        while(iterator.hasNext()){
            strTemp = iterator.next();
            try {
                end = sdf.parse(strTemp.split("&&")[0]);
            }catch(Exception error){
                try {
                    end = sdf.parse(this.dateFormater(strTemp.split("&&")[0]));
                }catch(Exception error2){
                    error2.printStackTrace();
                    tt++;
                }
            }
            long between=(end.getTime()-begin.getTime())/(1000*60*60);
            if(map.get(between) != null){
                map.get(between).add(strTemp);
            }
        }
        System.out.println("tttttttttttt: " + tt);
        return map;
    }

    public Map<Long,ArrayList<String>> initDataSummarySplit(List<String> list){
        Map<Long,ArrayList<String>> map = new HashMap<Long,ArrayList<String>>();
        for(long i = 0; i < 3912; i++){
            map.put(i, new ArrayList<String>());
        }
        Date begin = null;
        try {
            begin = sdf.parse("2014-04-01 00:00:00");
        }catch(Exception error){
            error.printStackTrace();
        }
        Date end = null;
        System.out.println("size: " + list.size());
        Iterator<String> iterator = list.iterator();
        String strTemp = "";
        String needDateFormater = "";
        int tt = 0;
        while(iterator.hasNext()){
            strTemp = iterator.next();
            try {
                end = sdf.parse(strTemp.split("&&")[0]);
            }catch(Exception error){
                try {
                    end = sdf.parse(this.dateFormater(strTemp.split("&&")[0]));
                }catch(Exception error2){
                    error2.printStackTrace();
                    tt++;
                }
            }
            long between=(end.getTime()-begin.getTime())/(1000*60*60);
            if(map.get(between) != null){
                map.get(between).add(strTemp);
            }
        }
        System.out.println("tttttttttttt: " + tt);
        return map;
    }

    /**
     * 按车站分
     * @return
     */
    public Map<Long,ArrayList<String>> initDataSummarySplit2(){
        Map<Long,ArrayList<String>> map = new HashMap<Long,ArrayList<String>>();
        for(long i = 0; i < 336; i++){
            map.put(i, new ArrayList<String>());
        }
        List<String> list = this.initDataSummary();
        Map<String,Long> clusterMap = this.initClusterData2();
        System.out.println("size: " + list.size());
        Iterator<String> iterator = list.iterator();
        String strTemp = "";
        String[] strArray = null;
        while(iterator.hasNext()){
            strTemp = iterator.next();
            strArray = strTemp.split("&&");
            if(clusterMap.get(strArray[1]+"&&"+strArray[2]) == null){
                continue;
            }
            map.get(clusterMap.get(strArray[1]+"&&"+strArray[2])).add(strTemp);
        }
        return map;
    }

    /**
     * 将时间格式不对的字符串进行转换
     * @return
     */
    public String dateFormater(String dateStr){
        String[] array = dateStr.split("/");
        String[] array2 = array[2].split(" ");
        if(array[0].length() < 2){
            array[0] = "0" + array[0];
        }
        if(array[1].length() < 2){
            array[1] = "0" + array[1];
        }
        return array2[0] + "-" + array[0] + "-" + array[1] + " " + array2[1];
    }

    /**
     * 将344个车站读取出来，并存在字典中
     * @return
     */
    public Map<String, Long> initClusterData2(){
        Map<String, Long> map = new HashMap<String,Long>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILEPATH + "cluster_9.txt"));
            String s = br.readLine();
            String[] array = null;
            long i = 0;
            while (s != null) {
                Pattern p = Pattern.compile("\t");
                Matcher m = p.matcher(s);
                s = m.replaceAll("&&");
                array = s.split("&&");
                map.put(array[0] + "&&" + array[1],i);
                i++;
                s = br.readLine();
            }
        }catch(Exception error){
            error.printStackTrace();
        }
        return map;
    }

    /**
     * 将344个车站读取出来，并存在字典中
     * @return
     */
    public List<Map<String,String>> initClusterData(){
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILEPATH + "cluster_9.txt"));
            String s = br.readLine();
            String[] array = null;
            while (s != null) {
                Map<String,String> map = new HashMap<String,String>();
                Pattern p = Pattern.compile("\t");
                Matcher m = p.matcher(s);
                s = m.replaceAll("&&");
                array = s.split("&&");
                map.put(array[0] + "&&" + array[1],array[2]);
                clusterDataMap.put(array[0] + "&&" + array[1],array[2]);
                list.add(map);
                s = br.readLine();
            }
        }catch(Exception error){
            error.printStackTrace();
        }
        return list;
    }

    public void exportExcel(){
        try {
            CityBikeTest test = new CityBikeTest();
            Map<Long, ArrayList<String>> mapSummarySplit = test.initDataSummarySplit();
            List<Map<String, String>> mapClusterList = test.initClusterData();
            Map<String, String> mapCluster = null;
            String cluster = "";
            CsvWriter csvWrite = null;
            List<String> listTemp = null;
            List<String> list = new ArrayList<String>();
            CityBikeTest test2 = new CityBikeTest();
            for (int i = 0; i < mapClusterList.size(); i++) {
                System.out.println("hhS: " + new Date());
                csvWrite = new CsvWriter(EXPORTFILEPATH + i + ".csv",',', Charset.forName("GBK"));
                mapCluster = mapClusterList.get(i);
                cluster = mapCluster.keySet().iterator().next();
                for (long j = 0; j < 3912; j++) {
                    //System.out.println("jj: " + new Date() + "   " + j);
                    listTemp = test2.item(mapSummarySplit.get(j),cluster);
                    //System.out.println("listTemp: " + listTemp);
                    try{
                        csvWrite.writeRecord(listTemp.toArray(new String[listTemp.size()]));
                        mapSummarySplit.get(j).removeAll(listNeedDel);
                        listNeedDel.clear();
                    }catch(Exception error){
                        error.printStackTrace();
                    }
                }
                System.out.println("hhE: " + new Date());
            }
        }catch(Exception error){
            error.printStackTrace();
        }

    }

    public void exportExcel2(){
        try {
            CityBikeTest test = new CityBikeTest();
            // 按车站分的
            Map<Long, ArrayList<String>> mapSummarySplit = test.initDataSummarySplit2();
            List<Map<String, String>> mapClusterList = test.initClusterData();
            Map<String, String> mapCluster = null;
            String cluster = "";
            CsvWriter csvWrite = null;
            List<String> listTemp = null;
            List<String> list = new ArrayList<String>();
            CityBikeTest test2 = new CityBikeTest();
            Map<Long, ArrayList<String>> mapSummarySplit2 = null;
            for (long i = 0; i < mapClusterList.size(); i++) {
                mapSummarySplit2 = test.initDataSummarySplit(mapSummarySplit.get(i));
                System.out.println("hhS: " + new Date());
                csvWrite = new CsvWriter(EXPORTFILEPATH + i + ".csv",',', Charset.forName("GBK"));
                mapCluster = mapClusterList.get((int)i);
                cluster = mapCluster.keySet().iterator().next();
                for (long j = 0; j < 3912; j++) {
                    //System.out.println("jj: " + new Date() + "   " + j);
                    listTemp = test2.item(mapSummarySplit2.get(j),cluster);
                    //System.out.println("listTemp: " + listTemp);
                    try{
                        csvWrite.writeRecord(listTemp.toArray(new String[listTemp.size()]));
                        csvWrite.flush();
                        //mapSummarySplit.get(j).removeAll(listNeedDel);
                        //listNeedDel.clear();
                    }catch(Exception error){
                        error.printStackTrace();
                        System.out.println("----------------------");
                    }
                }
                System.out.println("hhE: " + new Date());
            }
        }catch(Exception error){
            error.printStackTrace();
        }

    }

    public List<String> item(ArrayList<String> list, String startPoint){
        List<String> list2 = new ArrayList<String>();
        int num = 0;
        String strListPer = "";
        String[] strListArray = null;
        try{
            for(int i = 0; i<30; i++){
                for(int j = 0; j<list.size(); j++){
                    strListPer = list.get(j);
                    strListArray = strListPer.split("&&");
                    if((strListArray[1] + "&&" + strListArray[2]).equals(startPoint)
                            && clusterDataMap.get(strListArray[3] + "&&" + strListArray[4]) != null
                            && clusterDataMap.get(strListArray[3] + "&&" + strListArray[4]).equals(i + "")){
                        //listNeedDel.add(strListPer);
                        num++;
                    }
                }
                list2.add(num + "");
                num = 0;
            }
        }catch(Exception error){
            error.printStackTrace();
        }
        return list2;
    }

    public static void main(String[] args) {
        CityBikeTest test = new CityBikeTest();
        System.out.println(new Date());
        test.exportExcel2();
        //Map<Long,ArrayList<String>> map = test.initDataSummarySplit2();
        //System.out.println(map.keySet().size());
        //System.out.println(map.get(1l).size());
        System.out.println(new Date());
        System.out.println("Finish.......");
    }
}
