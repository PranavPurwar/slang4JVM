func int fact(int d)
	if (d <= 0)
		then
			return 1;
		else
			return d * fact(d - 1);
	endif
end

func boolean main()
	int d;
	d = 0;
	while (d <= 10)
		println fact(d);
		d = d + 1;
	wend
end