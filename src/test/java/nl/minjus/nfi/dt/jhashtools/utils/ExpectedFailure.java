package nl.minjus.nfi.dt.jhashtools.utils;

import org.apache.commons.lang.ArrayUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ExpectedFailure implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    // check for certain exception types
                    Optional annon = description.getAnnotation(Optional.class);
                    if (annon != null && ArrayUtils.contains(annon.exception(), e.getClass())) {
                        // ok
                    } else {
                        throw e;
                    }
                }
            }
        };
    }
}