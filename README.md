SLANG for JAVA VIRTUAL MACHINE
============

Vaisakh B
vaisakhb12@gmail.com

MIT License

## SLANG - INTRO ##
SLANG (Simple LANGuage) is a mature compiler infrastructure. Many versions of Slang are running in production environments as DSL’s and Transcompilers. Slang is available in many languages and is supported in all popular platforms. Initially Slang compiler (*SLANG4.NET*) was written in C# and generated .Net IL. Later it got ported to C++(with *LLVM as backend*), Java, Python.
~~~
*C#: http://slangfordotnet.codeplex.com/
*C++: https://github.com/pradeep-subrahmanion/SLANG4CPP
*Java Interpreter: https://github.com/aashiks/Slang4Java
*Python Interpreter: https://github.com/faisalp4p/slang-python
~~~
This project is Java port of Slang with byte code generation support. It uses The Byte Code Engineering Library from Apache to generate the byte code. Interpreter is also available. 
SLANG scripts are available in "scripts" subfolder of CallSlang directory

## GETTING STARTED ##
~~~
1. Make sure JDK and ANT are installed is available in the path. 
2. Clone the project.
3. Get the BCEL JAR from https://commons.apache.org/proper/commons-bcel/download_bcel.cgi and keep it in the roor folder.
4. Just run 'ant' command.
~~~

## EMBEDDING SLANG ##
Build Slang by running the build file present in the folder ‘slang_jvm’ (Remember to keep BCEL JAR in lib folder of ‘slang_jvm'). Then one can call slang in their program by creating an object of ‘com.slang.main.Slang’ class. Constructor of Slang has got following signature, `public Slang(String name, String code) throws Exception` (name: Name for slang program, code: Source code). And it has got two methods.
1. `public void interpret() throws Exception`: Interpretes the passed slang program.
2. `public void compile() throws Exception`:`Generates the Java class.
