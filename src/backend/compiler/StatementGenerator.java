package backend.compiler;

import java.util.*;

import antlr4.PascalParser;

import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

/**
 * <h1>StatementGenerator</h1>
 *
 * <p>Emit code for executable statements.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementGenerator extends CodeGenerator
{
    /**
     * Constructor.
     * @param parent the parent generator.
     * @param compiler the compiler to use.
     */
    public StatementGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }

    /**
     * Emit code for an assignment statement.
     * @param ctx the AssignmentStatementContext.
     */
    public void emitAssignment(PascalParser.AssignmentStatementContext ctx)
    {
        PascalParser.VariableContext   varCtx  = ctx.lhs().variable();
        PascalParser.ExpressionContext exprCtx = ctx.rhs().expression();
        SymtabEntry varId = varCtx.entry;
        Typespec varType  = varCtx.type;
        Typespec exprType = exprCtx.type;

        // The last modifier, if any, is the variable's last subscript or field.
        int modifierCount = varCtx.modifier().size();
        PascalParser.ModifierContext lastModCtx = modifierCount == 0
                            ? null : varCtx.modifier().get(modifierCount - 1);

        // The target variable has subscripts and/or fields.
        if (modifierCount > 0)
        {
            lastModCtx = varCtx.modifier().get(modifierCount - 1);
            compiler.visit(varCtx);
        }

        // Emit code to evaluate the expression.
        compiler.visit(exprCtx);

        // float variable := integer constant
        if (   (varType == Predefined.realType)
            && (exprType.baseType() == Predefined.integerType)) emit(I2F);

        // Emit code to store the expression value into the target variable.
        // The target variable has no subscripts or fields.
        if (lastModCtx == null) emitStoreValue(varId, varId.getType());

        // The target variable is a field.
        else if (lastModCtx.field() != null)
        {
            emitStoreValue(lastModCtx.field().entry, lastModCtx.field().type);
        }

        // The target variable is an array element.
        else
        {
            emitStoreValue(null, varType);
        }
    }

    /**
     * Emit code for an IF statement.
     * @param ctx the IfStatementContext.
     */
    public void emitIf(PascalParser.IfStatementContext ctx)
    {
        /***** Complete this method. *****/
        Label elseLabel = new Label();
        Label exitLabel = new Label();

        compiler.visit(ctx.expression());
        emit(IFEQ, elseLabel);
        compiler.visit(ctx.trueStatement());
        emit(GOTO, exitLabel);


        emitLabel(elseLabel);
        if (ctx.falseStatement() != null) {
            compiler.visit(ctx.falseStatement());
        }
        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    /**
     * Emit code for a CASE statement.
     * @param ctx the CaseStatementContext.
     */
    public void emitCase(PascalParser.CaseStatementContext ctx)
    {
        PascalParser.ExpressionContext exprCtx = ctx.expression();
        PascalParser.CaseBranchListContext branchListCtx = ctx.caseBranchList();
        ArrayList<Label> branchLabels = new ArrayList<Label>();

        branchLabels.add(new Label());  // exit label

        // Emit code to evaluate the SELECT expression.
        compiler.visit(exprCtx);

        // Process the select branches.
        TreeMap<Integer, Label> labelMap = createCaseMap(branchListCtx,
                branchLabels);

        // Emit code for the LOOKUPSWITCH and the branch statements.
        emitLookupSwitch(labelMap, branchLabels);
        emitBranchStatements(branchListCtx, branchLabels);
    }

    /**
     * Process the CASE map from the CASE branches.
     * @param branchListCtx the CaseBranchListContext.
     * @param branchLabels the branch labels.
     * @return the array list of table entries.
     */
    private TreeMap<Integer, Label> createCaseMap(
            PascalParser.CaseBranchListContext branchListCtx,
            ArrayList<Label> branchLabels)
    {
        TreeMap<Integer, Label> caseMap = new TreeMap<>();

        // Loop over the CASE branches.
        for (PascalParser.CaseBranchContext branchCtx :
                branchListCtx.caseBranch())
        {
            PascalParser.CaseConstantListContext constListCtx =
                    branchCtx.caseConstantList();

            if (constListCtx != null)
            {
                Label branchLabel = new Label();
                branchLabels.add(branchLabel);

                // Loop over the CASE constants of each CASE branch.
                for (PascalParser.CaseConstantContext caseConstCtx :
                        constListCtx.caseConstant())
                {
                    int value = caseConstCtx.value;
                    caseMap.put(value, branchLabel);
                }
            }
        }

        return caseMap;
    }

    /**
     * Emit code for the LOOKUPSWITCH instruction.
     * @param entries the table entries.
     * @param branchLabels the branch labels.
     */
    private void emitLookupSwitch(TreeMap<Integer, Label> labelMap,
                                  ArrayList<Label> branchLabels)
    {
        emitLine();
        emit(LOOKUPSWITCH);

        Set<Map.Entry<Integer, Label>> labelSet = labelMap.entrySet();
        Iterator<Map.Entry<Integer, Label>> it = labelSet.iterator();

        // For each entry, emit the value and its label.
        while (it.hasNext())
        {
            Map.Entry<Integer, Label> entry = it.next();
            emitLabel(entry.getKey(), entry.getValue());
        }

        // Emit the default label;
        emitLabel("default", branchLabels.get(0));
    }

    /**
     * Emit code for the branch statements.
     * @param branchListCtx the CaseBranchListContext.
     * @param branchLabels the branch labels
     * @throws PascalCompilerException if an error occurred.
     */
    private void emitBranchStatements(
            PascalParser.CaseBranchListContext branchListCtx,
            ArrayList<Label> branchLabels)
    {
        emitLine();

        // Loop to emit each branch label and then generate code for
        // the corresponding branch statement.
        int i = 1;
        for (PascalParser.CaseBranchContext branchCtx :
                branchListCtx.caseBranch())
        {
            if (branchCtx.caseConstantList() != null)
            {
                PascalParser.StatementContext stmtCtx = branchCtx.statement();

                emitLabel(branchLabels.get(i++));
                compiler.visit(stmtCtx);

                // Branch to the exit label.
                emit(GOTO, branchLabels.get(0));
            }
        }

        // Emit the exit label.
        emitLabel(branchLabels.get(0));

        emitLine();
    }


    /**
     * Emit code for a REPEAT statement.
     * @param ctx the RepeatStatementContext.
     */
    public void emitRepeat(PascalParser.RepeatStatementContext ctx)
    {
        Label loopTopLabel  = new Label();
        Label loopExitLabel = new Label();

        emitLabel(loopTopLabel);

        compiler.visit(ctx.statementList());
        compiler.visit(ctx.expression());
        emit(IFNE, loopExitLabel);
        emit(GOTO, loopTopLabel);

        emitLabel(loopExitLabel);
    }

    /**
     * Emit code for a WHILE statement.
     * @param ctx the WhileStatementContext.
     */
    public void emitWhile(PascalParser.WhileStatementContext ctx)
    {
        /***** Complete this method. *****/
        Label loopTopLabel  = new Label();
        Label loopExitLabel = new Label();

        emitLabel(loopTopLabel);

        compiler.visit(ctx.expression());
        emit(IFEQ, loopExitLabel);
        compiler.visit(ctx.statement());
        emit(GOTO, loopTopLabel);

        emitLabel(loopExitLabel);
    }

    /**
     * Emit code for a FOR statement.
     * @param ctx the ForStatementContext.
     */
    public void emitFor(PascalParser.ForStatementContext ctx)
    {
        /***** Complete this method. *****/
        Label loopTopLabel = new Label();
        Label trueLabel = new Label();
        Label loopExitLabel = new Label();

        compiler.visit(ctx.expression(0));

        // intialize control variable
        SymtabEntry varId = ctx.variable().entry;
        emitStoreValue(varId, varId.getType());

        emitLabel(loopTopLabel);

        // push control variable onto stack
        compiler.visit(ctx.variable());

        // check for loop condition
        compiler.visit(ctx.expression(1));
        if (ctx.TO() != null) {
            emit(IF_ICMPLE, trueLabel);
        }
        else {
            emit(IF_ICMPGE, trueLabel);
        }
        emit(GOTO, loopExitLabel);

        emitLabel(trueLabel);
        compiler.visit(ctx.statement());
        compiler.visit(ctx.variable());
        emit(ICONST_1);
        if (ctx.TO() != null) {
            emit(IADD);
        }
        else {
            emit(ISUB);
        }
        emitStoreValue(varId, varId.getType());
        emit(GOTO, loopTopLabel);

        emitLabel(loopExitLabel);
    }

    /**
     * Emit code for a procedure call statement.
     * @param ctx the ProcedureCallStatementContext.
     */
    public void emitProcedureCall(PascalParser.ProcedureCallStatementContext ctx)
    {
        /***** Complete this method. *****/
        SymtabEntry routineId = ctx.procedureName().entry;
        emitCall(routineId, ctx.argumentList());
    }

    /**
     * Emit code for a function call statement.
     * @param ctx the FunctionCallContext.
     */
    public void emitFunctionCall(PascalParser.FunctionCallContext ctx)
    {
        /***** Complete this method. *****/
        SymtabEntry routineId = ctx.functionName().entry;
        emitCall(routineId, ctx.argumentList());

        // A function call leaves a value on the operand stack.
        localStack.increase(1);
    }

    /**
     * Emit a call to a procedure or a function.
     * @param routineId the routine name's symbol table entry.
     * @param argListCtx the ArgumentListContext.
     */
    private void emitCall(SymtabEntry routineId,
                          PascalParser.ArgumentListContext argListCtx)
    {
        /***** Complete this method. *****/
        String routineName = routineId.getName();
        ArrayList<SymtabEntry> parmIds = routineId.getRoutineParameters();

        // Emit code to evaluate any arguments.
        int i = 0;
        if (argListCtx != null)
        {
            for (PascalParser.ArgumentContext argCtx : argListCtx.argument())
            {
                PascalParser.ExpressionContext exprCtx = argCtx.expression();
                compiler.visit(exprCtx);

                if (   (parmIds.get(i++).getType() == Predefined.realType)
                        && (exprCtx.type == Predefined.integerType))
                {
                    emit(I2F);
                }
            }
        }

        StringBuilder buffer = new StringBuilder();

        // Procedure or function name.
        buffer.append(programName);
        buffer.append("/");
        buffer.append(routineName);
        buffer.append("(");

        // Parameter and return type descriptors.
        if (argListCtx != null)
        {
            for (SymtabEntry parmId : parmIds)
            {
                buffer.append(typeDescriptor(parmId));
            }
        }
        buffer.append(")");
        buffer.append(typeDescriptor(routineId));

        // Generate a call to the routine.
        emit(INVOKESTATIC, buffer.toString());
        if (argListCtx != null) localStack.decrease(parmIds.size());
    }

    // emitList runs -ls on a given path. If no input is given it runs on the current path.
    public void emitList(PascalParser.ListStatementContext listCtx)
    {
        // Note: Lines 313-316 must be there
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        // Note: Lines 319-326 must be there
        emit(NEW, "Api");
        emit(DUP);
        emit(INVOKESPECIAL, "Api/<init>()V");
        if (listCtx.stringConstant() != null) {
            String path = listCtx.stringConstant().getText();
            emit(LDC, "\"" + path.substring(1, path.length()-1) + "\""); // changing from '' to ""
            // TODO - Change function name here
            emit(INVOKESTATIC, "Api/runListStatement(Ljava/lang/String;)V");
        }
        else {
            emit(INVOKESTATIC, "Api/runListStatement()V");
        }

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitPwd returns the working directory path
    public void emitPwd(PascalParser.PwdStatementContext pwdCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        emit(INVOKESTATIC, "Api/runPwdStatement()V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitCreateFile creates a file given file path as input
    public void emitCreateFile(PascalParser.CreateFileStatementContext createFileCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = createFileCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/createFileStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitCreateDir creates a directory given directory path as input
    public void emitCreateDir(PascalParser.CreateDirStatementContext createDirCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = createDirCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/createDirStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitRemoveFile removes a file path given as input
    public void emitRemoveFile(PascalParser.RemoveFileStatementContext removeFileCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = removeFileCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/removeFileOrDirStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitRemoveDir removes a directory path given as input
    public void emitRemoveDir(PascalParser.RemoveDirStatementContext removeDirCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = removeDirCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/removeFileOrDirStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitExec compiles and runs a file (Java, C, Python)
    public void emitExec(PascalParser.ExecStatementContext execCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = execCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/execStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitExecs compiles and runs a file (Java, C, Python) and stores the value
    public void emitExecs(PascalParser.ExecsStatementContext execsCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = execsCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/execsStatement(Ljava/lang/String;)I");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitOpen opens a file in its default application
    public void emitOpen(PascalParser.OpenStatementContext openCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = openCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/openStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitMove moves a file to a new location and deletes from old location
    public void emitMove(PascalParser.MoveStatementContext moveCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String oldPath = moveCtx.stringConstant(0).getText();
        String newPath = moveCtx.stringConstant(1).getText();
        emit(LDC, "\"" + oldPath.substring(1, oldPath.length()-1) + "\"");
        emit(LDC, "\"" + newPath.substring(1, newPath.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/moveStatement(Ljava/lang/String;Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitCopy creates a duplicate copy in new location
    public void emitCopy(PascalParser.CopyStatementContext copyCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String oldPath = copyCtx.stringConstant(0).getText();
        String newPath = copyCtx.stringConstant(1).getText();
        emit(LDC, "\"" + oldPath.substring(1, oldPath.length()-1) + "\"");
        emit(LDC, "\"" + newPath.substring(1, newPath.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/copyStatement(Ljava/lang/String;Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitPrintFile prints the raw contents of a file
    public void emitPrintFile(PascalParser.PrintFileStatementContext printFileCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String path = printFileCtx.stringConstant().getText();
        emit(LDC, "\"" + path.substring(1, path.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/printFileStatement(Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitShowStatement prints every instance of a phrase in a given file
    public void emitShowStatement(PascalParser.ShowStatementContext showCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String phrase = showCtx.stringConstant(0).getText();
        String fileName = showCtx.stringConstant(1).getText();
        emit(LDC, "\"" + phrase.substring(1, phrase.length()-1) + "\"");
        emit(LDC, "\"" + fileName.substring(1, fileName.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/showStatement(Ljava/lang/String;Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    // emitReplaceStatement replaces a phrase with the other input phrase in a given file
    public void emitReplaceStatement(PascalParser.ReplaceStatementContext replaceCtx)
    {
        Label topLabel  = new Label();
        Label exitLabel = new Label();

        emitLabel(topLabel);

        String oldPhrase = replaceCtx.stringConstant(0).getText();
        String fileName = replaceCtx.stringConstant(1).getText();
        String newPhrase = replaceCtx.stringConstant(2).getText();
        emit(LDC, "\"" + oldPhrase.substring(1, oldPhrase.length()-1) + "\"");
        emit(LDC, "\"" + fileName.substring(1, fileName.length()-1) + "\"");
        emit(LDC, "\"" + newPhrase.substring(1, newPhrase.length()-1) + "\"");
        emit(INVOKESTATIC, "Api/replaceStatement(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

        emit(GOTO, exitLabel);

        emitLabel(exitLabel);
    }

    /**
     * Emit code for a WRITE statement.
     * @param ctx the WriteStatementContext.
     */
    public void emitWrite(PascalParser.WriteStatementContext ctx)
    {
        emitWrite(ctx.writeArguments(), false);
    }

    /**
     * Emit code for a WRITELN statement.
     * @param ctx the WritelnStatementContext.
     */
    public void emitWriteln(PascalParser.WritelnStatementContext ctx)
    {
        emitWrite(ctx.writeArguments(), true);
    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     * @param needLF true if need a line feed.
     */
    private void emitWrite(PascalParser.WriteArgumentsContext argsCtx,
                           boolean needLF)
    {
        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (argsCtx == null)
        {
            emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");
            localStack.decrease(1);
        }

        // Generate code for the arguments.
        else
        {
            StringBuffer format = new StringBuffer();
            int exprCount = createWriteFormat(argsCtx, format, needLF);

            // Load the format string.
            emit(LDC, format.toString());

            // Emit the arguments array.
            if (exprCount > 0)
            {
                emitArgumentsArray(argsCtx, exprCount);

                emit(INVOKEVIRTUAL,
                     "java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Ljava/io/PrintStream;");
                localStack.decrease(2);
                emit(POP);
            }
            else
            {
                emit(INVOKEVIRTUAL,
                     "java/io/PrintStream/print(Ljava/lang/String;)V");
                localStack.decrease(2);
            }
        }
    }

    /**
     * Create the printf format string.
     * @param argsCtx the WriteArgumentsContext.
     * @param format the format string to create.
     * @return the count of expression arguments.
     */
    private int createWriteFormat(PascalParser.WriteArgumentsContext argsCtx,
                                  StringBuffer format, boolean needLF)
    {
        int exprCount = 0;
        format.append("\"");

        // Loop over the write arguments.
        for (PascalParser.WriteArgumentContext argCtx : argsCtx.writeArgument())
        {
            Typespec type = argCtx.expression().type;
            String argText = argCtx.getText();

            // Append any literal strings.
            if (argText.charAt(0) == '\'')
            {
                format.append(convertString(argText));
            }

            // For any other expressions, append a field specifier.
            else
            {
                exprCount++;
                format.append("%");

                PascalParser.FieldWidthContext fwCtx = argCtx.fieldWidth();
                if (fwCtx != null)
                {
                    String sign = (   (fwCtx.sign() != null)
                                   && (fwCtx.sign().getText().equals("-")))
                                ? "-" : "";
                    format.append(sign)
                          .append(fwCtx.integerConstant().getText());

                    PascalParser.DecimalPlacesContext dpCtx =
                                                        fwCtx.decimalPlaces();
                    if (dpCtx != null)
                    {
                        format.append(".")
                              .append(dpCtx.integerConstant().getText());
                    }
                }

                String typeFlag = type == Predefined.integerType ? "d"
                                : type == Predefined.realType    ? "f"
                                : type == Predefined.booleanType ? "b"
                                : type == Predefined.charType    ? "c"
                                :                                  "s";
                format.append(typeFlag);
            }
        }

        format.append(needLF ? "\\n\"" : "\"");

        return exprCount;
    }

    /**
     * Emit the printf arguments array.
     * @param argsCtx
     * @param exprCount
     */
    private void emitArgumentsArray(PascalParser.WriteArgumentsContext argsCtx,
                                    int exprCount)
    {
        // Create the arguments array.
        emitLoadConstant(exprCount);
        emit(ANEWARRAY, "java/lang/Object");

        int index = 0;

        // Loop over the write arguments to fill the arguments array.
        for (PascalParser.WriteArgumentContext argCtx :
                                                    argsCtx.writeArgument())
        {
            String argText = argCtx.getText();
            PascalParser.ExpressionContext exprCtx = argCtx.expression();
            Typespec type = exprCtx.type.baseType();

            // Skip string constants, which were made part of
            // the format string.
            if (argText.charAt(0) != '\'')
            {
                emit(DUP);
                emitLoadConstant(index++);

                compiler.visit(exprCtx);

                Form form = type.getForm();
                if (    ((form == SCALAR) || (form == ENUMERATION))
                     && (type != Predefined.stringType))
                {
                    emit(INVOKESTATIC, valueOfSignature(type));
                }

                // Store the value into the array.
                emit(AASTORE);
            }
        }
    }

    /**
     * Emit code for a READ statement.
     * @param ctx the ReadStatementContext.
     */
    public void emitRead(PascalParser.ReadStatementContext ctx)
    {
        emitRead(ctx.readArguments(), false);
    }

    /**
     * Emit code for a READLN statement.
     * @param ctx the ReadlnStatementContext.
     */
    public void emitReadln(PascalParser.ReadlnStatementContext ctx)
    {
        emitRead(ctx.readArguments(), true);
    }

    /**
     * Generate code for a call to READ or READLN.
     * @param argsCtx the ReadArgumentsContext.
     * @param needSkip true if need to skip the rest of the input line.
     */
    private void emitRead(PascalParser.ReadArgumentsContext argsCtx,
                          boolean needSkip)
    {
        int size = argsCtx.variable().size();

        // Loop over read arguments.
        for (int i = 0; i < size; i++)
        {
            PascalParser.VariableContext varCtx = argsCtx.variable().get(i);
            Typespec varType = varCtx.type;

            if (varType == Predefined.integerType)
            {
                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(INVOKEVIRTUAL, "java/util/Scanner/nextInt()I");
                emitStoreValue(varCtx.entry, null);
            }
            else if (varType == Predefined.realType)
            {
                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(INVOKEVIRTUAL, "java/util/Scanner/nextFloat()F");
                emitStoreValue(varCtx.entry, null);
            }
            else if (varType == Predefined.booleanType)
            {
                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(INVOKEVIRTUAL, "java/util/Scanner/nextBoolean()Z");
                emitStoreValue(varCtx.entry, null);
            }
            else if (varType == Predefined.charType)
            {
                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(LDC, "\"\"");
                emit(INVOKEVIRTUAL, "java/util/Scanner/useDelimiter(Ljava/lang/String;)" +
                                    "Ljava/util/Scanner;");
                emit(POP);
                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(INVOKEVIRTUAL, "java/util/Scanner/next()Ljava/lang/String;");
                emit(ICONST_0);
                emit(INVOKEVIRTUAL, "java/lang/String/charAt(I)C");
                emitStoreValue(varCtx.entry, null);

                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(INVOKEVIRTUAL, "java/util/Scanner/reset()Ljava/util/Scanner;");

            }
            else  // string
            {
                emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
                emit(INVOKEVIRTUAL, "java/util/Scanner/next()Ljava/lang/String;");
                emitStoreValue(varCtx.entry, null);
            }
        }

        // READLN: Skip the rest of the input line.
        if (needSkip)
        {
            emit(GETSTATIC, programName + "/_sysin Ljava/util/Scanner;");
            emit(INVOKEVIRTUAL, "java/util/Scanner/nextLine()Ljava/lang/String;");
            emit(POP);
        }
    }
}