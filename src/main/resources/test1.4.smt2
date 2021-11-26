OPTION "incremental";
OPTION "produce-models";
x,y : INT;
ASSERT (x > y AND y > 0);
ASSERT NOT(2*x-3*y >0 AND y >= 1);
QUERY 36*y <= 100;
COUNTERMODEL;