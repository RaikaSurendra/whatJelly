# Jelly Web Application - Learning Tutorial

A hands-on, chapter-based tutorial for learning Apache Jelly with custom tags in a web application context.

## 🎯 Learning Objectives

By completing this tutorial, you will:
- Understand how Jelly integrates with servlet-based web applications
- Learn to create custom tag libraries
- Master database-driven dynamic content generation
- Build real-world web interfaces with Jelly
- Understand the architecture similar to ServiceNow's Jelly implementation

---

## 📖 Tutorial Structure

### Chapter 1: Hello Jelly Web
**Duration**: 15 minutes  
**Difficulty**: Beginner  
**Page**: `/chapter1.jelly`

**Learning Objectives**:
- Understand basic Jelly syntax in a web context
- Learn how servlets process Jelly templates
- Display dynamic content from context variables

**Topics**:
- Basic Jelly tags (`<j:out>`, `<j:set>`)
- Request and session access
- Simple conditional logic

**Exercise**: Modify the page to display your name and current timestamp

---

### Chapter 2: Database Integration Basics
**Duration**: 20 minutes  
**Difficulty**: Beginner  
**Page**: `/chapter2.jelly`

**Learning Objectives**:
- Understand custom SQL tags
- Execute basic SELECT queries
- Display query results in HTML

**Topics**:
- `<app:sqlQuery>` tag syntax
- Iterating over result sets
- Basic data display patterns

**Exercise**: Query and display products under $50

---

### Chapter 3: Building Dynamic Lists
**Duration**: 25 minutes  
**Difficulty**: Beginner-Intermediate  
**Page**: `/chapter3.jelly`

**Learning Objectives**:
- Master loops and iteration
- Create styled data tables
- Implement conditional rendering

**Topics**:
- `<j:forEach>` with database results
- Conditional styling based on data
- Table generation patterns

**Exercise**: Create a user table with role-based badges

---

### Chapter 4: Forms and Data Manipulation
**Duration**: 30 minutes  
**Difficulty**: Intermediate  
**Page**: `/chapter4.jelly`

**Learning Objectives**:
- Handle form submissions
- Insert and update database records
- Understand POST request processing

**Topics**:
- HTML forms in Jelly
- `<app:sqlUpdate>` tag
- Request parameter access
- Success/error messages

**Exercise**: Build a form to add new products

---

### Chapter 5: Advanced Queries and Joins
**Duration**: 25 minutes  
**Difficulty**: Intermediate  
**Page**: `/chapter5.jelly`

**Learning Objectives**:
- Write complex SQL queries
- Perform table joins
- Aggregate data (COUNT, SUM, AVG)

**Topics**:
- Multi-table queries
- JOIN operations in custom tags
- Aggregate functions
- Grouping and ordering results

**Exercise**: Create a sales report with joined data

---

### Chapter 6: Custom Tag Creation
**Duration**: 45 minutes  
**Difficulty**: Advanced  
**Files**: Java source + Jelly page

**Learning Objectives**:
- Create your own custom tags
- Register tags in tag library
- Implement tag logic in Java

**Topics**:
- Extending `TagSupport`
- Tag attributes and body content
- Tag library registration
- Using custom tags in templates

**Exercise**: Create a `<app:highlight>` tag that highlights search terms

---

### Chapter 7: Building a Dashboard
**Duration**: 30 minutes  
**Difficulty**: Intermediate-Advanced  
**Page**: `/chapter7.jelly`

**Learning Objectives**:
- Combine multiple queries
- Create analytics visualizations
- Implement complex layouts

**Topics**:
- Multiple query coordination
- Calculating statistics
- CSS Grid for layout
- Chart-like displays with HTML/CSS

**Exercise**: Add a new metric to the dashboard

---

### Chapter 8: User Authentication Pattern
**Duration**: 40 minutes  
**Difficulty**: Advanced  
**Page**: `/chapter8.jelly`

**Learning Objectives**:
- Implement login/logout logic
- Session management
- Protected pages pattern

**Topics**:
- Session variables
- Password verification (simplified)
- Role-based access control
- Redirect patterns

**Exercise**: Add a "My Account" page for logged-in users

---

### Chapter 9: Real-World Application
**Duration**: 60 minutes  
**Difficulty**: Advanced  
**Page**: `/chapter9.jelly`

**Learning Objectives**:
- Build a complete CRUD interface
- Implement search functionality
- Add pagination

**Topics**:
- Create, Read, Update, Delete operations
- Search with LIKE queries
- Pagination with LIMIT/OFFSET
- URL parameter handling

**Exercise**: Add sorting to the product list

---

### Chapter 10: Performance and Best Practices
**Duration**: 30 minutes  
**Difficulty**: Advanced  
**Page**: `/chapter10.jelly`

**Learning Objectives**:
- Optimize queries
- Implement caching strategies
- Understand security considerations

**Topics**:
- Query optimization
- Connection pooling benefits
- SQL injection prevention
- XSS protection
- Performance monitoring

**Exercise**: Identify and fix performance issues in provided code

---

## 🎓 Certification Project

**Final Project**: Build a Mini E-Commerce System

**Requirements**:
1. Product catalog with categories
2. Shopping cart functionality
3. User registration and login
4. Order management
5. Admin panel for inventory

**Estimated Time**: 3-4 hours  
**Resources Provided**: Database schema, starter templates, hints

---

## 📚 Quick Reference

### Important Files
- `src/main/webapp/pages/chapter*.jelly` - Chapter examples
- `src/main/webapp/pages/exercises/` - Exercise files
- `src/main/webapp/pages/solutions/` - Solution files
- `src/main/java/com/example/webapp/tags/` - Custom tag source

### Running Chapters
```bash
# Start server
./run.sh

# Access chapters
http://localhost:8080/chapter1.jelly
http://localhost:8080/chapter2.jelly
# ... and so on
```

### Getting Help
- Check `TROUBLESHOOTING.md` for common issues
- Review `API_REFERENCE.md` for tag documentation
- Examine solution files if stuck

---

## 📝 Progress Tracking

Use this checklist to track your progress:

- [ ] Chapter 1: Hello Jelly Web
- [ ] Chapter 2: Database Integration Basics
- [ ] Chapter 3: Building Dynamic Lists
- [ ] Chapter 4: Forms and Data Manipulation
- [ ] Chapter 5: Advanced Queries and Joins
- [ ] Chapter 6: Custom Tag Creation
- [ ] Chapter 7: Building a Dashboard
- [ ] Chapter 8: User Authentication Pattern
- [ ] Chapter 9: Real-World Application
- [ ] Chapter 10: Performance and Best Practices
- [ ] Final Project: Mini E-Commerce System

---

## 🎯 Learning Tips

1. **Follow the order** - Chapters build on each other
2. **Do the exercises** - Hands-on practice is essential
3. **Experiment** - Modify examples and see what happens
4. **Check solutions** - But try exercises first!
5. **Take notes** - Document your learnings
6. **Build something** - Apply knowledge to your own project

---

## 🔄 Review and Practice

After completing chapters:
- Review chapter summaries
- Redo exercises from memory
- Try to explain concepts to someone else
- Build a small project without looking at examples

---

## 🚀 Next Steps After Completion

1. **Explore modern alternatives**: Thymeleaf, FreeMarker, React
2. **Study ServiceNow's implementation**: If you have access
3. **Build a portfolio project**: Use what you learned
4. **Share your knowledge**: Write a blog post or tutorial
5. **Contribute**: Help improve this tutorial

---

## 📖 Additional Resources

- [Apache Jelly Documentation](https://commons.apache.org/proper/commons-jelly/)
- [Servlet API Documentation](https://javaee.github.io/servlet-spec/)
- [H2 Database Documentation](https://h2database.com/)
- Parent project `try1/` for Apache Jelly fundamentals

---

**Happy Learning! 🎉**

Start with Chapter 1 and work your way through. Take your time and make sure you understand each concept before moving forward.
