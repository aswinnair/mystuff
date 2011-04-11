-- Turn a list into a palindrome, i.e. it should read the same both backwards and forwards. For example, given the list [1,2,3], your function should return [1,2,3,3,2,1


palify xs = nrevt(dup(nrev xs []))
     where dup x = (x,x)  
           nrev (x:xs) acc = nrev xs (x : acc)
           nrev [] acc = acc
           nrevt a = nrev (fst a) (snd a) 

rev (x:xs) acc = rev xs (x : acc)
rev [] acc = acc


isPali xs = checkPali xs (rev xs [])
      where checkPali (x:xs) (y:ys) | x /= y = False 
                                    | otherwise = checkPali xs ys
            checkPali [] [] = True
