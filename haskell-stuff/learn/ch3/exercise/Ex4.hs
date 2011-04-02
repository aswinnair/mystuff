-- Turn a list into a palindrome, i.e. it should read the same both backwards and forwards. For example, given the list [1,2,3], your function should return [1,2,3,3,2,1

pali :: [a] -> [a]

pali xs = xs ++ rev xs 

rev [] = []
rev (x:xs) = rev (xs) ++ [x]



-- This does not use the ++ operator to append list

npali xs = nrevt(dup(nrev xs []))

dup x = (x,x) 


nrev (x:xs) acc = nrev xs (x : acc)
nrev [] acc = acc

-- deconstruct a tuple and use the original function nrev
nrevt a = nrev (fst a) (snd a) 
