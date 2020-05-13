package entities;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * An abstraction of the to-do list. Each time the bot is activated, it fist reads the to-do
 * list form the yaml file "to-do.yml", deserialize it into this object.
 */
public class TodoList {
    private List<Todo> todoList;

    public TodoList() {
        todoList = new ArrayList<>();
    }

    public List<Todo> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "todoList=" + todoList +
                '}';
    }


    public Map<Long, String> toMap() {
        Map<Long, String> map = new HashMap<>();
        for (Todo t : todoList) {
            StringBuilder builder = new StringBuilder();
            builder.append(t.getId()).append(" ")
                    .append(t.getDueDate()).append(" ")
                    .append(t.getContent()).append(" ");
            for (Long id: t.getUserIds()) {
                builder.append(id).append(" ");
            }
            map.put(t.getId(), builder.toString().trim());
        }
        return map;
    }

    public void insert(Todo todo) {
        todoList.add(todo);
    }

    public void delete(Todo todo) {
        todoList.remove(todo);
    }

    /**
     * Represents the entries in the to-do list. It can be compared by the order of
     * dueDates.
     */
    public static class Todo implements Comparable<Todo>{
        private Long id;
        private Date dueDate;
        private String content;
        private Long[] userIds;
        private Long messageId;

        public Todo(Date dueDate, String content, Long[] userIds) {
            id = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            this.dueDate = dueDate;
            this.content = content;
            this.userIds = userIds;
        }

        public Todo() {
            id = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            messageId = -1L;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getDueDate() {
            return dueDate;
        }

        public void setDueDate(Date dueDate) {
            this.dueDate = dueDate;
        }

        public String getContent() {
            return content;
        }

        public Long getMessageId() {
            return messageId;
        }

        public void setMessageId(Long messageId) {
            this.messageId = messageId;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long[] getUserIds() {
            return userIds;
        }

        public void setUserIds(Long[] userIds) {
            this.userIds = userIds;
        }

        @Override
        public int compareTo(TodoList.Todo todo) {
            return this.getDueDate().compareTo(((Todo) todo).getDueDate());
        }

        @Override
        public String toString() {
            return "Todo{" +
                    "id=" + id +
                    ", dueDate=" + dueDate +
                    ", content='" + content + '\'' +
                    ", userIds=" + Arrays.toString(userIds) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Todo todo = (Todo) o;
            return Objects.equals(getId(), todo.getId()) &&
                    Objects.equals(getDueDate(), todo.getDueDate()) &&
                    Objects.equals(getContent(), todo.getContent()) &&
                    Arrays.equals(getUserIds(), todo.getUserIds());
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(getId(), getDueDate(), getContent());
            result = 31 * result + Arrays.hashCode(getUserIds());
            return result;
        }
    }
}
