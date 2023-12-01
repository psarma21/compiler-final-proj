PROGRAM Test;

VAR x : string;

BEGIN
  x := EXECS 'EXECTestFiles/HelloWorld.java';
  writeln(x);
END.
