.class public Test
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static x I

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	Test/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this LTest;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; MAIN
;
.method public static main([Ljava/lang/String;)V
.var 0 is args [Ljava/lang/String;
.var 1 is _start Ljava/time/Instant;
.var 2 is _end Ljava/time/Instant;
.var 3 is _elapsed J

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_1

;
; 006 x:=EXECS'EXECTestFiles/Demo.java'
;
L001:
	ldc	"EXECTestFiles/Demo.java"
	invokestatic	Api/execsStatement(Ljava/lang/String;)I
	goto	L002
L002:
	putstatic	Test/x I
;
; 007 writeln(x)
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	Test/x I
	invokestatic	java/lang/Integer/valueOf(I)Ljava/lang/Integer;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
;
; 010 CASExOF1:OPEN'cat.jpeg';2:OPEN'dog.jpeg';3:OPEN'mouse.jpeg';END
;
	getstatic	Test/x I

	lookupswitch
	  1: L004
	  2: L005
	  3: L006
	  default: L003

L004:
;
; 011 OPEN'cat.jpeg'
;
L007:
	ldc	"cat.jpeg"
	invokestatic	Api/openStatement(Ljava/lang/String;)V
	goto	L008
L008:
	goto	L003
L005:
;
; 012 OPEN'dog.jpeg'
;
L009:
	ldc	"dog.jpeg"
	invokestatic	Api/openStatement(Ljava/lang/String;)V
	goto	L010
L010:
	goto	L003
L006:
;
; 013 OPEN'mouse.jpeg'
;
L011:
	ldc	"mouse.jpeg"
	invokestatic	Api/openStatement(Ljava/lang/String;)V
	goto	L012
L012:
	goto	L003
L003:


	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_2
	aload_1
	aload_2
	invokestatic	java/time/Duration/between(Ljava/time/temporal/Temporal;Ljava/time/temporal/Temporal;)Ljava/time/Duration;
	invokevirtual	java/time/Duration/toMillis()J
	lstore_3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n[%,d milliseconds execution time.]\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	lload_3
	invokestatic	java/lang/Long/valueOf(J)Ljava/lang/Long;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

	return

.limit locals 6
.limit stack 20
.end method
