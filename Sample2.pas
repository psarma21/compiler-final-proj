{Fixing an errored Java file by replacing mispelled words}
program Sample2;


begin
  EXEC 'test-files/FixMe.java';

  SHOW 'peint' in 'test-files/FixMe.java';
  REPLACE 'peint' in 'test-files/FixMe.java' to 'print';

  EXEC 'test-files/FixMe.java';
end.