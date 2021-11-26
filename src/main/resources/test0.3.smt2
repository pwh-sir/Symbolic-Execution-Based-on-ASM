OPTION "incremental";
OPTION "produce-models";
x,y : INT;
ASSERT x > y AND y > 0;
QUERY x - y > 0;
COUNTERMODEL;