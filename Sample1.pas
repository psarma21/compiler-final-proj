{Demoing basic file/directory management}
program Sample1;

{Creates a folder, copies a file into that folder, runs file}
procedure makeFirstFile;
    begin
        PRINTPATH;
        CREATEDIR 'MyFolder';

        COPY 'test-files/HelloWorld.java' TO 'MyFolder/HelloWorld.java';
        LIST;
        PRINTFILE 'MyFolder/HelloWorld.java';
        EXEC 'MyFolder/HelloWorld.java';
    end;

{Removes folder and files created above}
procedure removeFirstFile;
    begin
        REMOVEFILE 'MyFolder/HelloWorld.java';
        REMOVEFILE 'MyFolder/HelloWorld.class';
        REMOVEDIR 'MyFolder';
    end;

begin
   makeFirstFile();
   removeFirstFile();
end.