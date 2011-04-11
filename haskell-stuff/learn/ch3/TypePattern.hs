 {-# LANGUAGE ScopedTypeVariables #-}

data Node  a = Leaf a | Branch (Node a) (Node a) | Tip  deriving (Show)


depth (Branch a b) = 1 + (max (depth a) (depth b))
depth (Leaf _) = 1 
depth (Tip) = 0



