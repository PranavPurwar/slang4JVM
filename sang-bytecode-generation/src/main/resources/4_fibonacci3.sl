FUNCTION BOOLEAN MAIN()
	NUMERIC prevTerm;
	NUMERIC currentTerm;
	
	prevTerm = 0;
	currentTerm = 1;
	
	WHILE (currentTerm < 100)
		PRINTLINE currentTerm; 
		currentTerm = currentTerm + prevTerm;
		prevTerm = currentTerm - prevTerm;
	WEND
END