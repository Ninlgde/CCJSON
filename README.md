# JZen
A Java high performance JSON library

### benchmark

file length:1676562

testFastJson start -- Fri Apr 17 02:50:57 CST 2020

parse resources/data/citm_catalog.json    9.795 ms    163.229 MB/s
   
dump resources/data/citm_catalog.json    3.482 ms
    
testFastJson end -- Fri Apr 17 02:51:10 CST 2020


testJZenNormal start -- Fri Apr 17 02:54:04 CST 2020

parse resources/data/citm_catalog.json    9.084 ms    176.018 MB/s
   
dump resources/data/citm_catalog.json    3.061 ms
    
testJZenNormal end -- Fri Apr 17 02:54:16 CST 2020


testJZenZeroCopy start -- Fri Apr 17 02:54:44 CST 2020

parse resources/data/citm_catalog.json    4.074 ms    404.286 MB/s
   
dump resources/data/citm_catalog.json    1.399 ms
    
testJZenZeroCopy end -- Fri Apr 17 02:54:49 CST 2020


----------------------------------------------------------------------------------------------------------------

file length:2251042

testFastJson start -- Fri Apr 17 02:55:21 CST 2020

parse resources/data/canada.json    19.573 ms    109.678 MB/s
   
dump resources/data/canada.json    22.013 ms
    
testFastJson end -- Fri Apr 17 02:56:03 CST 2020


testJZenNormal start -- Fri Apr 17 02:56:24 CST 2020

parse resources/data/canada.json    11.191 ms    191.829 MB/s
   
dump resources/data/canada.json    5.209 ms
    
testJZenNormal end -- Fri Apr 17 02:56:41 CST 2020


testJZenZeroCopy start -- Fri Apr 17 02:57:00 CST 2020

parse resources/data/canada.json    8.945 ms    240.008 MB/s
   
dump resources/data/canada.json    2.592 ms
    
testJZenZeroCopy end -- Fri Apr 17 02:57:12 CST 2020


----------------------------------------------------------------------------------------------------------------

file length:552445

testFastJson start -- Fri Apr 17 02:57:58 CST 2020

parse resources/data/twitter.json    4.351 ms    121.099 MB/s
   
dump resources/data/twitter.json    1.590 ms
    
testFastJson end -- Fri Apr 17 02:58:04 CST 2020


testJZenNormal start -- Fri Apr 17 02:58:51 CST 2020

parse resources/data/twitter.json    4.941 ms    106.631 MB/s
   
dump resources/data/twitter.json    2.761 ms
    
testJZenNormal end -- Fri Apr 17 02:58:59 CST 2020


testJZenZeroCopy start -- Fri Apr 17 02:59:13 CST 2020

parse resources/data/twitter.json    2.243 ms    268.488 MB/s
   
dump resources/data/twitter.json    1.245 ms
    
testJZenZeroCopy end -- Fri Apr 17 02:59:16 CST 2020

