{Fixing an errored Java file by replacing mispelled words}
program Sample2;


begin
  EXEC 'EXECTestFiles/FixMe.java';

  SHOW 'peint' in 'EXECTestFiles/FixMe.java';
  REPLACE 'peint' in 'EXECTestFiles/FixMe.java' to 'print';

  EXEC 'EXECTestFiles/FixMe.java';
end.