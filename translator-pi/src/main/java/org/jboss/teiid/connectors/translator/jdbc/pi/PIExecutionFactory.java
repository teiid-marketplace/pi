/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.jboss.teiid.connectors.translator.jdbc.pi;

import static org.teiid.translator.TypeFacility.RUNTIME_NAMES.FLOAT;
import static org.teiid.translator.TypeFacility.RUNTIME_NAMES.INTEGER;
import static org.teiid.translator.TypeFacility.RUNTIME_NAMES.OBJECT;
import static org.teiid.translator.TypeFacility.RUNTIME_NAMES.STRING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.teiid.language.Function;
import org.teiid.language.Limit;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.SourceSystemFunctions;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.jdbc.AliasModifier;
import org.teiid.translator.jdbc.ConvertModifier;
import org.teiid.translator.jdbc.FunctionModifier;
import org.teiid.translator.jdbc.JDBCExecutionFactory;

@Translator(name="osisoft-pi", description="A translator for OsiSoft PI database")
public class PIExecutionFactory extends JDBCExecutionFactory {
    public static String PI = "pi"; //$NON-NLS-1$
    protected ConvertModifier convert = new ConvertModifier();
    
    public PIExecutionFactory() {
        setUseBindVariables(false);
    }
    
    @Override
    public void start() throws TranslatorException {
        super.start();

        convert.addTypeMapping("Int8", FunctionModifier.BYTE); //$NON-NLS-1$
        convert.addTypeMapping("Int16", FunctionModifier.SHORT); //$NON-NLS-1$
        convert.addTypeMapping("Int32", FunctionModifier.INTEGER); //$NON-NLS-1$
        convert.addTypeMapping("Int64", FunctionModifier.LONG); //$NON-NLS-1$
        convert.addTypeMapping("UInt8", FunctionModifier.BYTE); //$NON-NLS-1$
        convert.addTypeMapping("UInt16", FunctionModifier.SHORT); //$NON-NLS-1$
        convert.addTypeMapping("UInt32", FunctionModifier.INTEGER); //$NON-NLS-1$
        convert.addTypeMapping("UInt64", FunctionModifier.LONG); //$NON-NLS-1$
        convert.addTypeMapping("Single", FunctionModifier.FLOAT); //$NON-NLS-1$
        convert.addTypeMapping("Double", FunctionModifier.DOUBLE); //$NON-NLS-1$
        convert.addTypeMapping("Boolean", FunctionModifier.BOOLEAN); //$NON-NLS-1$
        convert.addTypeMapping("AnsiString", FunctionModifier.STRING); //$NON-NLS-1$
        convert.addTypeMapping("String", FunctionModifier.STRING); //$NON-NLS-1$
        convert.addTypeMapping("DateTime", FunctionModifier.TIMESTAMP); //$NON-NLS-1$
        convert.addTypeMapping("Time", FunctionModifier.TIME); //$NON-NLS-1$
        convert.addTypeMapping("Variant", FunctionModifier.OBJECT); //$NON-NLS-1$

        registerFunctionModifier(SourceSystemFunctions.CONVERT, convert);
        registerFunctionModifier(SourceSystemFunctions.MOD, new AliasModifier("%")); //$NON-NLS-1$
        registerFunctionModifier(SourceSystemFunctions.DAYOFYEAR, new AliasModifier("DAY")); //$NON-NLS-1$
        registerFunctionModifier(SourceSystemFunctions.LOCATE, new FunctionModifier() {
            @Override
            public List<?> translate(Function function) {
                function.setName("INSTR"); //$NON-NLS-1$
                if (function.getParameters().get(2) == null) {
                    return Arrays.asList(function.getParameters().get(1), function.getParameters().get(0));
                }
                else {
                    return Arrays.asList(function.getParameters().get(1), function.getParameters().get(0), function.getParameters().get(2));
                }
            }
        });        
        registerFunctionModifier(SourceSystemFunctions.LCASE, new AliasModifier("LOWER")); //$NON-NLS-1$
        registerFunctionModifier(SourceSystemFunctions.UCASE, new AliasModifier("UPPER")); //$NON-NLS-1$
        registerFunctionModifier(SourceSystemFunctions.SUBSTRING, new AliasModifier("SUBSTR")); //$NON-NLS-1$
        
        addPushDownFunction(PI, "COSH", FLOAT, FLOAT); //$NON-NLS-1$
        addPushDownFunction(PI, "TANH", FLOAT, FLOAT); //$NON-NLS-1$
        addPushDownFunction(PI, "SINH", FLOAT, FLOAT); //$NON-NLS-1$
        addPushDownFunction(PI, "FORMAT", STRING, FLOAT, STRING); //$NON-NLS-1$
        addPushDownFunction(PI, "FORMAT", STRING, INTEGER, STRING); //$NON-NLS-1$
        addPushDownFunction(PI, "ParentName", STRING, STRING, INTEGER); //$NON-NLS-1$
        addPushDownFunction(PI, "List", STRING, STRING)
            .setVarArgs(true); //$NON-NLS-1$
        addPushDownFunction(PI, "DIGCODE", INTEGER, STRING, STRING); //$NON-NLS-1$
        addPushDownFunction(PI, "DIGSTRING", STRING, INTEGER); //$NON-NLS-1$
        addPushDownFunction(PI, "PE", STRING, OBJECT); //$NON-NLS-1$
        
    }

    @Override
    public boolean supportsSelectWithoutFrom() {
        return true;
    }
    
    @Override
    public boolean supportsInlineViews() {
        return false;
    }
    
    @Override
    public boolean supportsRowLimit() {
        return true;
    }
    
    @Override
    public boolean supportsFunctionsInGroupBy() {
        return true;
    }    
    
    @Override
    public boolean supportsInsertWithQueryExpression() {
        return false;
    }    
    
    @Override
    public boolean supportsBatchedUpdates() {
        return false;
    }
    
    @Override
    public boolean supportsBulkUpdate() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<?> translateLimit(Limit limit, ExecutionContext context) {
        return Arrays.asList("TOP ", limit.getRowLimit()); //$NON-NLS-1$
    }
    
    @Override
    public boolean useSelectLimit() {
        return true;
    }    

    @Override
    public List<String> getSupportedFunctions() {
        List<String> supportedFunctions = new ArrayList<String>();
        supportedFunctions.addAll(super.getSupportedFunctions());

        supportedFunctions.add(SourceSystemFunctions.ABS);
        supportedFunctions.add(SourceSystemFunctions.ACOS);
        supportedFunctions.add(SourceSystemFunctions.ASIN);
        supportedFunctions.add(SourceSystemFunctions.ATAN);
        supportedFunctions.add(SourceSystemFunctions.ATAN2);
        supportedFunctions.add(SourceSystemFunctions.CEILING);
        supportedFunctions.add(SourceSystemFunctions.COALESCE);
        supportedFunctions.add(SourceSystemFunctions.CONCAT);
        supportedFunctions.add(SourceSystemFunctions.COS);        
        supportedFunctions.add(SourceSystemFunctions.CONVERT);
        supportedFunctions.add(SourceSystemFunctions.DAYOFYEAR);
        supportedFunctions.add(SourceSystemFunctions.EXP);
        supportedFunctions.add(SourceSystemFunctions.FLOOR);
        supportedFunctions.add(SourceSystemFunctions.HOUR);
        supportedFunctions.add(SourceSystemFunctions.LCASE);
        supportedFunctions.add(SourceSystemFunctions.LOCATE);
        supportedFunctions.add(SourceSystemFunctions.LEFT);
        supportedFunctions.add(SourceSystemFunctions.LENGTH);
        supportedFunctions.add(SourceSystemFunctions.LTRIM);
        supportedFunctions.add(SourceSystemFunctions.LOG);
        supportedFunctions.add(SourceSystemFunctions.LOG10);
        supportedFunctions.add(SourceSystemFunctions.MINUTE);
        supportedFunctions.add(SourceSystemFunctions.MOD);
        supportedFunctions.add(SourceSystemFunctions.POWER);
        supportedFunctions.add(SourceSystemFunctions.SECOND);
        supportedFunctions.add(SourceSystemFunctions.SQRT);
        supportedFunctions.add(SourceSystemFunctions.REPLACE);
        supportedFunctions.add(SourceSystemFunctions.RIGHT);
        supportedFunctions.add(SourceSystemFunctions.ROUND);
        supportedFunctions.add(SourceSystemFunctions.RTRIM);
        supportedFunctions.add(SourceSystemFunctions.MONTH);
        supportedFunctions.add(SourceSystemFunctions.NULLIF);
        supportedFunctions.add(SourceSystemFunctions.PI);
        supportedFunctions.add(SourceSystemFunctions.SIN);
        supportedFunctions.add(SourceSystemFunctions.SUBSTRING);
        supportedFunctions.add(SourceSystemFunctions.TAN);
        supportedFunctions.add(SourceSystemFunctions.TRIM);
        supportedFunctions.add(SourceSystemFunctions.UCASE);
        supportedFunctions.add(SourceSystemFunctions.YEAR);        
        return supportedFunctions;
    }
}
