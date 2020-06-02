# ASM_Instrumentor
Java ASM byte code instrumentor to calculate Code Coverage and Regression Testing

IMPORTANT NOTE: You won't be able to see instrumentations when using an IDE, they'll only be displayed in the console when using a terminal. 
In the case that you're using a IDE, you would use a .txt file to mimic the output and to check for coverage.

This project works by creating the complete instrumention of the whole class using commands:
“Compiling Instrumenter”
javac -cp asm-8.0.1.jar Instrumenter.java
”Compiling C”
javac C.java
"Instrumenting C"
java -cp .:asm-8.0.1.jar Instrumenter C.class C.class
"Compiling Test"
javac Main.java
"Testing input"
java Main

This will create the instrumented .class for the C.class and will save it to a ClassRecord.txt and, if being used in terminal, display it to the console.

This project also checks the differences between C.class and a second version/update to C.class and logs issues, if any are found, that violate regression testing as Dangerous Edges in danRecord.txt
After this, the project will check certain test cases and see if they cover any of the dangerous edges created from the altered/updated code and will log them into an Arraylist. 
