func boolean printNumber(int A)
	println A;
	return true;
end

func boolean main()
	int D;
	D = 0;
	boolean temp;
	while (D <= 100) 
		temp = printNumber(D);
		D = D + 1;
	wend
end