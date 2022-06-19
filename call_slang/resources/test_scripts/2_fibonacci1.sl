func boolean main()
	int newTerm;
	int prevTerm;
	int currentTerm;
	
	currentTerm = 1;
	prevTerm = 0;
	newTerm = currentTerm + prevTerm;
	
	println prevTerm;
	println currentTerm;
	
	while (newTerm < 1000) 
		println newTerm;
		prevTerm = currentTerm;
		currentTerm = newTerm;
		newTerm = currentTerm + prevTerm;
	wend
end