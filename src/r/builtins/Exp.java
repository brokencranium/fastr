package r.builtins;

import com.oracle.truffle.api.frame.Frame;
import r.*;
import r.data.*;
import r.data.internal.View;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.RNode;

public class Exp {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg) {
                    RDouble typedArg;

                    if (arg instanceof RDouble) {
                        typedArg = (RDouble) arg;
                    } else if (arg instanceof RInt || arg instanceof RLogical) {
                        typedArg = arg.asDouble();
                    } else {
                        if (arg instanceof RComplex) {
                            // TODO missing exp for complex numbers
                            Utils.nyi("complex exp");
                        }
                        throw RError.getNonNumericMath(ast);
                    }

                    return new View.RDoubleProxy<RDouble>(typedArg) {

                        @Override
                        public double getDouble(int i) {
                            double d = orig.getDouble(i);
                            if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                                return RDouble.NA;
                            } else {
                                double res = Math.exp(d);
                                if (RDouble.RDoubleUtils.isNAorNaN(res)) {
                                    RContext.warning(ast, RError.NAN_PRODUCED);
                                }
                                return res;
                            }
                        }
                    };
                }

            };
        }
    };
}