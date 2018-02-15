package query.condition;

import constants.ObjectType;
import model.Commit;
import model.GitObject;
import query.statements.Condition;

import java.util.Date;

public class FilterParser {
    public static boolean checkCondition(GitObject gitObject, QueryFilter queryFilter) throws FilterParserException {
        switch (queryFilter.getObject()) {
            case SHA1:
                return checkSha1(gitObject, queryFilter.getFilter(), queryFilter.getValue());
            case AUTHOR:
                return checkAuthor(gitObject, queryFilter.getFilter(), queryFilter.getValue());
            case EMAIL:
                return checkEmail(gitObject, queryFilter.getFilter(), queryFilter.getValue());
            case MESSAGE:
                return checkMessage(gitObject, queryFilter.getFilter(), queryFilter.getValue());
            case DATE:
                return checkDate(gitObject, queryFilter.getFilter(), queryFilter.getValue());
            default:
                return false;

        }
    }

    private static boolean checkDate(GitObject gitObject, Condition filter, String value) {
        if (gitObject.getType() == ObjectType.COMMIT) {
            Commit obj = (Commit) gitObject;
            return checkDateCondition(obj.getDate(), value, filter);
        }
        return false;
    }



    private static boolean checkMessage(GitObject gitObject, Condition filter, String value) throws FilterParserException {
        if (gitObject.getType() == ObjectType.COMMIT) {
            Commit obj = (Commit) gitObject;
            return checkStringCondition(obj.getComment(), value, filter);
        }
        return false;
    }

    private static boolean checkEmail(GitObject gitObject, Condition filter, String value) throws FilterParserException {
        if (gitObject.getType() == ObjectType.COMMIT) {
            Commit obj = (Commit) gitObject;
            return checkStringCondition(obj.getEmail(), value, filter);
        }
        return false;
    }

    private static boolean checkAuthor(GitObject gitObject, Condition filter, String value) throws FilterParserException {
        if (gitObject.getType() == ObjectType.COMMIT) {
            Commit obj = (Commit) gitObject;
            return checkStringCondition(obj.getAuthor(), value, filter);
        }
        return false;

    }

    private static boolean checkSha1(GitObject gitObject, Condition filter, String value) throws FilterParserException {
        return checkStringCondition(gitObject.getSha1(), value, filter);
    }

    private static boolean checkStringCondition(String objectValue, String conditionValue, Condition filter) throws FilterParserException {
        if(objectValue==null){
            return false;
        }
        switch (filter) {
            case IS:
            case EQUAL:
                return objectValue.equals(conditionValue);
            case CONTAINS:
                return objectValue.contains(conditionValue);
            case STARTS_WITH:
                return objectValue.startsWith(conditionValue);
            case GREATER_THAN:
            case LESS_THAN:
            default:
                throw new FilterParserException("Illegal condition for sha1 type: " + filter);
        }
    }
    private static boolean checkDateCondition(Date date, String value, Condition filter) {
        return false;
    }

}
