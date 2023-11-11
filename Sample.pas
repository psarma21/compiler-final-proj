program Sample;

procedure makeFirstFile;
    begin
        PRINTDIRPATH;
        CREATEDIR 'folder1';
        LIST;
        CHANGEDIR 'folder1';

        CREATEFILE 'file1.java';
        OPEN 'file1.java';
        PRINTFILE 'file1.java';
        EXEC 'file1.java';

        CHANGEDIR '..';
    end;

procedure makeSecondFile;
    begin
        PRINTDIRPATH;
        LIST;
        CREATEDIR 'folder2';
        LIST;
        CHANGEDIR 'folder2';

        COPY '../folder1/file1.java' 'file2.java';
        OPEN 'file2.java';
        PRINTFILE 'file2.java';
        EXEC 'file2.java';

        CHANGEDIR '..';
    end;

procedure removeSecondFile;
    begin
        LIST;
        CHANGEDIR 'folder2';
        LIST;

        REMOVEFILE 'file2.java';
        REMOVEFILE 'file2.class';
        CHANGEDIR '..';
        REMOVEDIR 'folder2';

        LIST;
    end;

begin
   makeFirstFile();
   makeSecondFile();
   removeSecondFile();
end.