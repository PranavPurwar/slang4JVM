FUNCTION BOOLEAN MAIN()
	NUMERIC newTerm;
	NUMERIC prevTerm;
	NUMERIC currentTerm;
	
	currentTerm = 1;
	prevTerm = 0;
	newTerm = currentTerm + prevTerm;
	
	PRINTLINE prevTerm;
	PRINTLINE currentTerm;
	
	WHILE (newTerm < 1000) 
		PRINTLINE newTerm;
		prevTerm = currentTerm;
		currentTerm = newTerm;
		newTerm = currentTerm + prevTerm;
	WEND
END