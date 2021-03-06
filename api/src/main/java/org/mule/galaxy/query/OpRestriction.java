package org.mule.galaxy.query;

import java.util.Collection;

public class OpRestriction extends Restriction {
    public enum Operator {
        EQUALS,
        NOT,
        IN,
        LIKE, 
        OR,
        AND
    }

    private Object value;
    private Object left;
    private Operator operator;
    
    protected OpRestriction(Operator o, Object left, Object right) {
        this.operator = o;
        this.left = left;
        this.value = right;
    }
    
    protected OpRestriction(Operator not, OpRestriction restriction) {
        this.operator = not;
        this.value = restriction;
    }

    public Object getRight() {
        return value;
    }

    public Object getLeft() {
        return left;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }
    
    public void toString(StringBuilder sb) {
        toString(sb, false);
    }
    
    private void toString(StringBuilder sb, boolean not) {
        switch (operator) {
        case EQUALS:
            sb.append(left);
            if (not) {
                sb.append(" != '");
            } else {
                sb.append(" = '");
            }
            sb.append(value);
            sb.append("'");
            break;
        case LIKE:
            sb.append(left);
            if (not) {
                sb.append(" not");
            } 
            sb.append(" like '");
            sb.append(value);
            sb.append("'");
            break;
        case IN:
            sb.append(left);
            sb.append(" in ('");
            
            boolean first = true;
            for (Object val : ((Collection) value)) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(val)
                  .append("'");
                
            }
            sb.append(")");
            
            break;
        case OR:
            Restriction or1 = (Restriction) left;
            Restriction or2 = (Restriction) value;
            sb.append("(");
            or1.toString(sb);
            sb.append(" or ");
            or2.toString(sb);
            sb.append(")");
            
            break;
        case AND:
            Restriction and1 = (Restriction) left;
            Restriction and2 = (Restriction) value;
            sb.append("(");
            and1.toString(sb);
            sb.append(" and ");
            and2.toString(sb);
            sb.append(")");
            
            break;
        case NOT:
            ((OpRestriction) value).toString(sb, true);
            break;
        }
    }

    public static OpRestriction eq(String property, Object value) {
        return new OpRestriction(Operator.EQUALS, property, value);
    }

    public static OpRestriction not(OpRestriction restriction) {
        return new OpRestriction(Operator.NOT, restriction);
    }

    public static OpRestriction like(String property, Object value)
    {
        return new OpRestriction(Operator.LIKE, property, value);
    }

    public static OpRestriction in(String property, Collection<?> values) {
        return new OpRestriction(Operator.IN, property, values);
    }

    public static OpRestriction or(Restriction left, Restriction right) {
        return new OpRestriction(Operator.OR, left, right);
    }

    public static OpRestriction and(Restriction left, Restriction right) {
        return new OpRestriction(Operator.AND, left, right);
    }
}

