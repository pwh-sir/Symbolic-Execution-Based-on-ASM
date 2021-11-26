OPTION "incremental";
OPTION "produce-models";
x,y : INT;
ASSERT NOT(x > y AND y > 0);
CHECKSAT x > 40;
COUNTERMODEL;