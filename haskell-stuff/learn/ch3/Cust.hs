-- try different data constructor styles

type CustId = Int
type CustName = String
type Address = [String]


-- positional 
data Customer = Cust CustId CustName Address   deriving (Show)

oldA = Cust   1224  "aswin" ["blah", "blaa"]

-- this does not work due to change order
-- oldA = Cust   "aswin"  1234 ["blah", "blaa"]



-- by name, so order is not important as above
data CustomerNew = CustN {custId::CustId, custName::CustName, address::Address}  deriving (Show)

newA = CustN{custName = "aswin", custId = 1224, address = ["blah", "blaa"]}

