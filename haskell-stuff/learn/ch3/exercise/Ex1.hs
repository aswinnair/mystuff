
mylen :: [a] -> Int

mylen [] = 0 
mylen [x] = 1
mylen (x:xs) = mylen (xs) + 1 



mylen1 xs =  lfunc 0 xs 
    where lfunc acc (x:ixs) =  lfunc (acc + 1) ixs 
          lfunc acc [] = acc


printLen [] = "empty"
printLen (x:y:xs) = "Atleast two"
printLen (x:xs) = "Atleast oneg"
