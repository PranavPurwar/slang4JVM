package com.slang.ast;

import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Alls AST node's implemnts this interface (Visitor Pattern)
public interface Visitable {
	Symbol accept(Context context, Visitor visitor) throws Exception;
}
