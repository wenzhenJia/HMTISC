HMTISC: Human Mobility Prediction based on Trend Iteration of Spectral Clustering

Codes: https://github.com/wenzhenJia/HMTISC

Datasets: BikeNYC and BikeDC are the datasets we used in the paper, it suffices to reproduce the results what we have reported in the paper.

Download BikeNYC dataset provided by ./data/NYCdata/ in https://drive.google.com/file/d/1OQXaE6g4EsuMdISiHroerUfpXqTWWKOk/view?usp=sharing
Download BikeDC dataset provided by ./data/DCdata/ in https://drive.google.com/file/d/1OQXaE6g4EsuMdISiHroerUfpXqTWWKOk/view?usp=sharing

Description:

Step 1. Run ./Spectral Clustering/Ng_Jordan_Weiss.m
Perform spectral clustering on location information (latitude, longitude) or on trend features (location information, Frobenius norm of mobility trend matrix A).

Step 2. Run ./Hierarchical_processing/Mobility_trend_dataprocessing/datademo/src/CityBikeTest
Generate the mobility trend matrix A. 

Step 3. Run ./Hierarchical_processing/Mobility_trend_dataprocessing/fandemo/fandemo.m 
Generate the Frobenius norm of matrix A.

Step 4. Run ./Hierarchical_processing/Generate_datasets/Generate_datasets.py 
Generate a dual-channel matrix as input for ST-ResNet and ST-3DNet.

Step 5. Run ./STResNet/deepst/datasets/BikeNYC.py or ./STResNet/deepst/datasets/BikeDC.py
Run ST-ResNet prediction.

Step 6. Run ./ST3DNet/deepst/trainNY.py or ./ST3DNet/deepst/trainDC.py
Run ST-3DNet prediction.
