func boolean main()
	int prevTerm;
	int currentTerm;
	
	prevTerm = 0;
	currentTerm = 1;
	
	while (currentTerm < 100)
		println currentTerm; 
		currentTerm = currentTerm + prevTerm;
		prevTerm = currentTerm - prevTerm;
	wend
end