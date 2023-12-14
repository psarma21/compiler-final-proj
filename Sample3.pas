{Demoing Pascal and Bash integrating together}
PROGRAM Sample3;

var x: integer;

BEGIN
  {Storing the output of a file into a variable and doing something with it}
  x := EXECS 'test-files/Demo.java';
  writeln(x);

  CASE x OF
    1:  OPEN 'images/cat.jpeg';
    2:  OPEN 'images/dog.jpeg';
    3:  OPEN 'images/mouse.jpeg';
  END;

END.
