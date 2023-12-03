PROGRAM Test;

var x: integer;

BEGIN
  x := EXECS 'EXECTestFiles/Demo.java';
  writeln(x);

  CASE x OF
    1:       OPEN 'cat.jpeg';
    2:      OPEN 'cat.jpeg';
    3:      OPEN 'cat.jpeg';
  END;


END.
