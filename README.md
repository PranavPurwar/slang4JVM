#SLANG with Bytecode Generation Support#
============

Vaisakh B
vaisakhb12@gmail.com

MIT License

## SLANG - INTRO ##
SLANG (Simple LANGuage) is a mature compiler infrastructure. Many versions of Slang are running in production environments as DSL’s and Transcompilers. Slang is available in many languages and is supported in all popular platforms. Initially Slang compiler (*SLANG4.NET*) was written in C# and generated .Net IL. Later it got ported to C++(with *LLVM as backend*), Java, Python.
*C#: http://slangfordotnet.codeplex.com/
*C++: https://github.com/pradeep-subrahmanion/SLANG4CPP
*Java Interpreter: https://github.com/aashiks/Slang4Java
*Python Interpreter: https://github.com/faisalp4p/slang-python
This project is Java port of Slang with byte code generation support. It uses The Byte Code Engineering Library from Apache to generate the byte code. Interpreter is also available. 
SLANG scripts are available in "scripts" subfolder of CallSlang directory

## GETTING STARTED ##
*Make sure JDK and ANT are installed is available in the path. 
*Clone the project.
*Get the BCEL JAR from [here](https://commons.apache.org/proper/commons-bcel/download_bcel.cgi) and keep it in the roor folder.
*Just run ANT

## EMBEDDING SLANG ##
*Build Slang by running the build file present in the folder ‘slang_bytecode’ (Remember to keep BCEL Jar in lib folder of ‘slang_bytecode). 
*One can call slang in their program by creating an object of ‘com.slang.main.Slang’ class. Constructor has got following signature.
	*public Slang(String name, String code) throws Exception
	name: Name of the module, code: Source code .
	And it has got two methods:
		*public void interpret() throws Exception
			Interpretes the passed slang program.
		*public void compile() throws Exception
			Generates the Java class.



