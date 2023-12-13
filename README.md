# Project Bashcal

## Introduction
A compiler developed for "Bashcal", a language created by combining parts of Pascal and Bash. While utilizing
existing Pascal functionalities, it adds basic Bash-like capabilities to create an overall powerful language for first-time
Bash users.

## Grammar

| Bashcal                                | Description                                                       | Usage                                            | Notes                                        |
|----------------------------------------|-------------------------------------------------------------------|--------------------------------------------------|----------------------------------------------|
| LIST `path`                            | Lists all items in current directory                              | ```LIST;```                                      | Default (no path given) is current directory |
| PRINTPATH                              | Prints working directory                                          | ```PRINTPATH;```                                 |                                              |
| OPEN `file`                            | Opens file in default application (ex: text editor for txt files) | ```OPEN 'myfile.txt';```                         |                                              |
| MOVE `file_or_dir` TO `file_or_dir`    | Moves file/dir from old path to new path                          | ```MOVE '../myfile.txt' TO 'mynewfile.txt';```   |                                              |
| COPY `file_or_dir` TO `file_or_dir`    | Creates copy of file/dir and copies to new path                   | ```COPY '../myfile.txt' TO 'mynewfile.txt';```   |                                              |
| CREATEFILE `file`                      | Creates file                                                      | ```CREATEFILE 'myfile.java';```                  |                                              |
| CREATEDIR `dir`                        | Creates directory                                                 | ```CREATEDIR 'myfolder';```                      |                                              |
| REMOVEFILE `file`                      | Removes file                                                      | ```REMOVEFILE 'myfile.java';```                  |                                              |
| REMOVEDIR `dir`                        | Removes directory                                                 | ```REMOVEDIR 'myfolder';```                      |                                              |
| PRINTFILE `file`                       | Prints contents of file                                           | ```PRINTFILE 'myfile.java';```                   |                                              |
| EXEC `file`                            | Compiles + runs a program                                         | ```EXEC 'myfile.java';```                        | Currently supports only Java, C files        |
| EXECS `file`                           | Compiles + runs a program + stores output to variable             | ```x := EXECS 'myfile.java';```                  | Currently supports only Java, C files        |
| SHOW `phrase` IN `file`                | Prints all lines containing phrase in file                        | ```SHOW 'hello' in 'myfile.java';```             |                                              |
| REPLACE `phrase` IN `file` TO `phrase` | Replaces all instances of phrase with new phrase in file          | ```REPLACE 'hello' in 'myfile.java' to 'bye';``` |                                              |



## Usage
1. Write a program in Bashcal. Let's call the file ```Sample.pas```. Please refer to the grammar above to write a sample program in Bashcal.

2. Create associated Jasmine file:
```
java src/Pascal.java -compile Sample.pas
```

3. Convert Jasmin file to Java class:
```
java -jar jasmin-2.4/jasmin.jar Sample.j
```

4. Run Java class:
```
java Sample.class
```
