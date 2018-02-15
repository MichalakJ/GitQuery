package query;

import model.Commit;
import model.GitObject;
import model.Repository;
import org.apache.log4j.Logger;
import query.condition.FilterParser;
import query.condition.FilterParserException;
import query.condition.LogicType;
import query.condition.QueryFilter;
import query.exception.QueryProcessorException;
import query.statements.Expression;
import query.statements.Condition;
import query.statements.Object;
import reader.GitRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class QueryProcessor {
    static final Logger logger = Logger.getLogger(QueryProcessor.class);

    private GitRepository repository;
    private Repository repositoryData;

    public QueryProcessor(GitRepository repository) throws IOException, DataFormatException {
        this.repository = repository;
        repositoryData = repository.readRepository();
    }

    public String query(String query) throws QueryProcessorException, FilterParserException {
        if (query.startsWith(Expression.SELECT.getValue() + " ")) {
            return processSelectQuery(query.substring(Expression.SELECT.getValue().length() + 1));
        } else {
            throw new QueryProcessorException("Invalid statement, failed to parse first word");
        }
    }

    private String processSelectQuery(String query) throws QueryProcessorException, FilterParserException {
        String word = query.substring(0, query.indexOf(" "));
        int length = word.length() + 1;
        String statements[] = word.split("\\.");
        Object currentObject = null;
        List<Object> objectList = new ArrayList<>();
        for (String object : statements) {
            currentObject = Object.getObject(object);
            if (currentObject == null) {
                throw new QueryProcessorException("Invalid statement, field not found: " + object);
            } else {
                objectList.add(currentObject);
            }
        }
        return processWhereQuery(query.substring(length, query.length()), objectList);
    }

    private String processWhereQuery(String query, List<Object> objectList) throws QueryProcessorException, FilterParserException {
        String[] parts = query.split("\\s+");
        List<QueryFilter> filterList = new ArrayList<>();
        if (!parts[0].equals(Expression.WHERE.getValue())) {
            throw new QueryProcessorException("Invalid statement, failed to parse WHERE word");
        }
        int filterArgument = 0;
        QueryFilter queryFilter = null;
        for (int i = 1; i < parts.length; i++) {
            filterArgument++;
            if (filterArgument == 1) {
                queryFilter = new QueryFilter();
                Object object = Object.getObject(parts[i]);
                if (object == null) {
                    throw new QueryProcessorException("Invalid statement, failed to parse object, object not found: " + parts[i]);
                }
                queryFilter.setObject(object);
            } else if (filterArgument == 2) {
                Condition filter = Condition.getCondition(parts[i]);
                if (filter == null) {
                    throw new QueryProcessorException("Invalid statement, failed to parse filter, filter not found: " + parts[i]);
                }
                queryFilter.setFilter(filter);
            } else if (filterArgument == 3) {
                String value = parts[i];
                queryFilter.setValue(value);
                filterList.add(queryFilter);

            } else if (filterArgument == 4) {
                try {
                    LogicType logicType = LogicType.valueOf(parts[i]);
                    queryFilter.setLogicType(logicType);
                    filterArgument = 0;
                } catch (Exception e) {
                    throw new QueryProcessorException("Invalid statement, expected AND,OR");
                }

            }
        }
        if (filterArgument != 0 && filterArgument != 3) {
            throw new QueryProcessorException("Invalid statement, failed to parse condition");
        }
        addObjectTypeToFilter(filterList, objectList.get(0));
        return getData(objectList, filterList);

    }

    private void addObjectTypeToFilter(List<QueryFilter> filterList, Object object) {
        QueryFilter queryFilter = new QueryFilter();
        queryFilter.setLogicType(LogicType.AND);
        queryFilter.setObject(Object.TYPE);
        queryFilter.setFilter(Condition.EQUAL);
        queryFilter.setValue(object.getValue());
    }

    private String getData(List<Object> objectList, List<QueryFilter> filterList) throws FilterParserException {
        StringBuilder sb = new StringBuilder();
        List<GitObject> eligibleObjects = new ArrayList<>();
        for (GitObject gitObject : repositoryData.getObjects().values()) {
            boolean eligible = true;
            LogicType logicType = null;
            for (QueryFilter queryFilter : filterList) {
                boolean conditionResult = FilterParser.checkCondition(gitObject, queryFilter);
                if (logicType != null) {
                    if (logicType == LogicType.AND) {
                        eligible = eligible && conditionResult;
                    } else if (logicType == LogicType.OR) {
                        eligible = eligible || conditionResult;
                    }
                } else {
                    eligible = conditionResult;
                }
                logicType = queryFilter.getLogicType();
            }
            if (eligible) {
                eligibleObjects.add(gitObject);
            }
        }
        return extractData(objectList, eligibleObjects);
    }

    private String extractData(List<Object> objectList, List<GitObject> eligibleObjects) {
        StringBuilder sb = new StringBuilder();
        if (objectList.size() == 1) {
            for (GitObject eligibleObject : eligibleObjects) {
                switch (objectList.get(0)) {
                    case COMMIT:
                        Commit commit = (Commit) eligibleObject;
                        sb.append(commit.toString());
                        break;
                    default:
                        sb.append(eligibleObject.toString());
                        break;
                }
                sb.append("\n");
            }
        } else {
            for (GitObject eligibleObject : eligibleObjects) {
                switch (objectList.get(1)) {
                    case SHA1:
                        sb.append(eligibleObject.getSha1());
                        break;
                }
                if (objectList.get(0) == Object.COMMIT) {
                    Commit commit = (Commit) eligibleObject;
                    switch (objectList.get(1)) {
                        case AUTHOR:
                            sb.append(commit.getAuthor());
                            break;
                        case EMAIL:
                            sb.append(commit.getEmail());
                            break;
                        case MESSAGE:
                            sb.append(commit.getComment());
                            break;
                        case DATE:
                            sb.append(commit.getDate());
                            break;

                    }
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }


}
