mydrop :: Int -> [a] -> [a]
mydrop n xs =   if n <=0 || null xs 
		then xs
		else mydrop (n - 1) (tail xs)
