OPTION "incremental";
OPTION "produce-models";
x,y : INT;
ASSERT NOT(x > y AND y > 0);
QUERY x > 40;
COUNTERMODEL;