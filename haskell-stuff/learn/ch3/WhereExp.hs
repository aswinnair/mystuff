lend amount withdraw =  if bal < threshold 
			then Nothing 
			else Just bal 
	where bal = amount - withdraw 
	      threshold = 100


-- Just testing if it returns an function
retFunc b = blah 
    where blah a =  a * a
