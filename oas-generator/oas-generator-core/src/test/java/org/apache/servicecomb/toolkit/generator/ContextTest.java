/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.toolkit.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;

import org.apache.servicecomb.toolkit.generator.AnnotationProcessorTest.OpenapiDef;
import org.apache.servicecomb.toolkit.generator.context.OasContext;
import org.apache.servicecomb.toolkit.generator.context.OperationContext;
import org.apache.servicecomb.toolkit.generator.context.ParameterContext;
import org.apache.servicecomb.toolkit.generator.context.ParameterContext.InType;
import org.junit.Test;

import io.swagger.v3.oas.models.PathItem.HttpMethod;

public class ContextTest {

  @Test
  public void realParameterContext() throws NoSuchMethodException {
    Method method = OpenapiDef.class.getDeclaredMethod("hello", String.class, Object.class);
    OasContext oasContext = new OasContext(null);
    OperationContext operationContext = new OperationContext(method, oasContext);

    Parameter parameter = method.getParameters()[0];
    ParameterContext context = new ParameterContext(operationContext, parameter);
    context.setRequired(true);
    context.setSchema(null);

    assertTrue(context.isRequired());
    context.setDefaultValue("default");
    context.setIn(InType.QUERY);
    context.setName("param1");
    context.addExtension("extension-key", "extension-value");

    assertFalse(context.isRequestBody());

    context.toParameter();
    context.applyAnnotations(Collections.emptyList());

    assertEquals("param1", context.getName());
    assertEquals(InType.QUERY, context.getIn());
    assertEquals(String.class, context.getRealType());
    assertEquals(String.class, context.getType());
    assertEquals(operationContext.getComponents(), context.getComponents());
    assertEquals(operationContext, context.getOperationContext());
    assertNull(context.getExtensions());
    assertNotNull(context.getOasParameter());
    assertNull(context.getParser());
    assertEquals(parameter, context.getParameter());
    assertNotNull(context.getSchema());
    assertEquals("default", context.getDefaultValue());
    assertFalse(context.isRequired());

    context.setIn(InType.COOKIE);
    assertFalse(context.isRequestBody());
    context.toParameter();

    context.setIn(InType.HEADER);
    assertFalse(context.isRequestBody());
    context.toParameter();

    context.setIn(InType.PATH);
    assertFalse(context.isRequestBody());
    context.toParameter();

    context.setIn(InType.FORM);
    assertTrue(context.isRequestBody());
    context.toParameter();

    context.setIn(InType.BODY);
    context.toParameter();
    assertTrue(context.isRequestBody());
  }

  @Test
  public void operationContext() throws NoSuchMethodException {
    Method method = OpenapiDef.class.getDeclaredMethod("hello", String.class, Object.class);
    OasContext oasContext = new OasContext(null);
    OperationContext operationContext = new OperationContext(method, oasContext);
    Parameter parameter = method.getParameters()[0];
    ParameterContext context = new ParameterContext(operationContext, parameter);
    context.setIn(InType.FORM);

    operationContext.setHttpMethod(HttpMethod.GET.name());
    operationContext.toOperation();

    assertEquals(HttpMethod.GET.name(), operationContext.getHttpMethod());
  }
}
