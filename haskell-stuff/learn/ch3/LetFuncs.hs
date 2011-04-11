
format word count = map print count 
	where   print 0 = "Zero " ++ word 
		print 1 = "One " ++ word
		print n = show(n) ++ " " ++ word ++ "s"

-- now lets this with the same name for the internal function


-- this works and shows inner function is in scope
bformat word count = map bformat count 
	where   bformat 0 = "Zero " ++ word 
		bformat 1 = "One " ++ word
		bformat n = show(n) ++ " " ++ word ++ "s"

var :: [Int]
var = [1,2,3,4,5]


a :: String -> Int -> String
a word count = word ++ " is " ++ show(count)
