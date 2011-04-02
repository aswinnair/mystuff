
myBal bal withdrawal = let threshold = 100 
			   newB = bal - withdrawal
         	       in if newB < threshold 
		       	  then Nothing 
			  else Just newB 



foo = let a = 100 
      in let b = 200 
         in a + b  


fooconst = let a = 1 
      in let b = 2 
         in 3
