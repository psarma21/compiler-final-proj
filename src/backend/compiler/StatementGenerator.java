package backend.compiler;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;

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
        /***** Complete this method. *****/
        Label caseBeginLabel = new Label();
        Label caseBranchBeginLabel = new Label();
        Label caseBranchIncrementLabel = new Label();
        Label caseExitLabel = new Label();

//        int caseBranchIndex = 0;
//        int caseBranchConstantIndex = 0;

        emitLabel(caseBeginLabel);
        compiler.visit(ctx.expression());
        emit(GOTO, caseBranchBeginLabel);

        emitLabel(caseBranchBeginLabel);
//        compiler.visit(ctx.caseBranchList().caseBranch(caseBranchIndex).caseConstantList().caseConstant(caseBranchConstantIndex));
//        emit(IFNE, caseBranchBeginLabel);
//        compiler.visit(ctx.caseBranchList().caseBranch(caseBranchIndex).statement());
//        emit(GOTO, caseExitLabel);
        int count = 0;
        for (PascalParser.CaseBranchContext caseBranch : ctx.caseBranchList().caseBranch()) {
            System.out.println(count);
            count++;
            if (caseBranch.statement() != null) {
                for (PascalParser.CaseConstantContext caseConstant : caseBranch.caseConstantList().caseConstant()) {
                    compiler.visit(caseConstant.constant());
                }

                compiler.visit(caseBranch.statement());
            }
        }

//        emitLabel(caseBranchIncrementLabel);
//        caseBranchIndex++;
//        emit(GOTO, caseBranchBeginLabel);

        emitLabel(caseExitLabel);
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
        Label procedureTopLabel  = new Label();
        Label procedureExitLabel = new Label();

        emitLabel(procedureTopLabel);
        emitCall(ctx.procedureName().entry, ctx.argumentList());
        emit(GOTO, procedureExitLabel);

        emitLabel(procedureExitLabel);
    }

    /**
     * Emit code for a function call statement.
     * @param ctx the FunctionCallContext.
     */
    public void emitFunctionCall(PascalParser.FunctionCallContext ctx)
    {
        /***** Complete this method. *****/
        Label functionTopLabel  = new Label();
        Label functionExitLabel = new Label();

        emitLabel(functionTopLabel);
        emitCall(ctx.functionName().entry, ctx.argumentList());
        emit(GOTO, functionExitLabel);

        emitLabel(functionExitLabel);
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
        if (argListCtx != null) {
            int index = 0;
            for (PascalParser.ArgumentContext arg : argListCtx.argument()) {
                compiler.visit(arg);
                if (!typeDescriptor(arg.expression().type).equals(typeDescriptor(routineId.getRoutineParameters().get(index).getType()))) {
                    // int-to-float conversion
                    emit(I2F);
                }
                index++;
            }
        }

        /***** Complete this method. *****/
        String classPath = programName + "/" + routineId.getName();
        String typeSignature = "(";
        for (SymtabEntry s : routineId.getRoutineParameters()) {
            typeSignature += typeDescriptor(s.getType());
        }
        typeSignature += ")" + typeDescriptor(routineId);

        String invokeStaticInput = classPath + typeSignature;
        emit(INVOKESTATIC, invokeStaticInput);
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