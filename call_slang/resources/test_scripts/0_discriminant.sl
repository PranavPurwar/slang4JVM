func int getDiscriminant(int a, int b, int c)
	int n;
	n = b * b - 4 * a * c;
	
	if (n < 0) 
		then 
			return 0;
		else 
			if (n == 0) 
				then
					return 1;
				else 
					return 2;
			endif
	endif
end

func boolean main()
	int d;
	D = getDiscriminant(1, -2, -4);
	
	if (d == 0) 
		then
			print "no roots";
		else
			if (d == 1)
				then
					print "unique root present";
				else 
					print "Two roots available";
			endif
	endif
end