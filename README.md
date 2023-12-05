Project Bashcal: A compiler developed for "Bashcal", a language created by combining parts of Pascal and Bash. While utilizing 
existing Pascal functionalities, it adds basic Bash-like capabilities to create an overall powerful language for first-time 
Bash users.

Setup: Write a program in Bashcal in a .pas file (such as Test.pas) and run the following commands:

1. Create associated Jasmine file:
```
java src/Pascal.java -compile your_bashcal_file_name.pas
```

2. Convert Jasmin file to Java class:
```
java -jar jasmin-2.4/jasmin.jar generated_jasmine_file.j
```

3. Run Java class:
```
java generated_java_class.class
```
