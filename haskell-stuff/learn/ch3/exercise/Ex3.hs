-- Write a function that computes the mean of a list, i.e. the sum of all elements in the list divided by its length. 
--   (You may need to use the fromIntegral function to convert the length of the list from an integer into a floating point number.) 



mean xs = avg(lmean (0,0) xs)
     where lmean (acc,len) [] = (acc,len) 
           lmean (acc,len) (x:xs) =  lmean (acc + x, len + 1) xs 
           avg (tot, len) = tot / len

