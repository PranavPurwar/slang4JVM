func int fibonacci(int n)
	if (n == 0)
		then
			return 0;
		else
			if (n == 1)
				then
					return 1;
				else 
					return fibonacci(n-1) + fibonacci(n-2);
			endif
	endif
end

func boolean main()
	int d;
	d = 0;
	while (d <= 10)
		println fibonacci(d);
		d = d + 1;
	wend
end
