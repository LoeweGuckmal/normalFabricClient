package ch.loewe.normal_use_client.fabricclient.account;

import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.Contract;

public class Expression {
    private final String expression;
    private int position;

    public Expression(String expression) {
        this.expression = expression.replaceAll("\\s+", "");
    }

    @Contract(
            pure = true
    )
    private char current() {
        return this.position >= this.expression.length() ? '?' : this.expression.charAt(this.position);
    }

    public double parse() {
        return this.parse(0);
    }

    private double parse(int returnFlag) {
        try {
            double x;
            if (returnFlag == 2) {
                if (this.current() == '-') {
                    ++this.position;
                    return -this.parse(2);
                } else if (this.current() == '(') {
                    ++this.position;
                    x = this.parse(0);
                    if (this.current() == ')') {
                        ++this.position;
                    }

                    if (this.current() == '^') {
                        ++this.position;
                        x = Math.pow(x, this.parse(2));
                    }

                    return x;
                } else {
                    int begin;
                    for(begin = this.position; this.current() == '.' || Character.isDigit(this.current()); ++this.position) {
                    }

                    x = Double.parseDouble(this.expression.substring(begin, this.position));
                    if (this.current() == '^') {
                        ++this.position;
                        x = Math.pow(x, this.parse(2));
                    }

                    return x;
                }
            } else {
                x = this.parse(2);

                while(true) {
                    while(this.current() != '*') {
                        if (this.current() != '/') {
                            if (returnFlag == 1) {
                                return x;
                            }

                            while(true) {
                                while(this.current() != '+') {
                                    if (this.current() != '-') {
                                        return x;
                                    }

                                    ++this.position;
                                    x -= this.parse(1);
                                }

                                ++this.position;
                                x += this.parse(1);
                            }
                        }

                        ++this.position;
                        x /= this.parse(2);
                    }

                    ++this.position;
                    x *= this.parse(2);
                }
            }
        } catch (Throwable var5) {
            throw new IllegalArgumentException("Sorry, but we're unable to calculate " + this.expression + " at pos " + this.position, var5);
        }
    }

    @Contract(
            pure = true
    )
    public static double parseWidthHeight(String expression, int width, int height) throws IllegalArgumentException {
        return (new Expression(Objects.requireNonNullElse(expression, "").toLowerCase(Locale.ROOT).replace("w", String.valueOf(width)).replace("h", String.valueOf(height)))).parse();
    }
}
