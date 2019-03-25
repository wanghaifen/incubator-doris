// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.optimizer.operator;

import org.apache.doris.analysis.JoinOperator;
import org.apache.doris.optimizer.OptExpression;
import org.apache.doris.optimizer.OptExpressionWapper;
import org.apache.doris.optimizer.base.OptColumnRefSet;
import org.apache.doris.optimizer.base.OptLogicalProperty;
import org.apache.doris.optimizer.rule.OptRuleType;
import org.apache.doris.optimizer.stat.DefaultStatistics;
import org.apache.doris.optimizer.stat.RowCountProvider;
import org.apache.doris.optimizer.stat.Statistics;
import org.apache.doris.optimizer.stat.StatisticsContext;

import java.util.BitSet;

public class OptLogicallJoin extends OptLogical {

    private JoinOperator operator;

    public OptLogicallJoin() {
        super(OptOperatorType.OP_LOGICAL_JOIN);
    }

    @Override
    public BitSet getCandidateRulesForExplore() {
        final BitSet set = new BitSet();
        set.set(OptRuleType.RULE_EXP_JOIN_COMMUTATIVITY.ordinal());
        set.set(OptRuleType.RULE_EXP_JOIN_ASSOCIATIVITY.ordinal());
        return set;
    }

    @Override
    public BitSet getCandidateRulesForImplement() {
        final BitSet set = new BitSet();
        set.set(OptRuleType.RULE_IMP_EQ_JOIN_TO_HASH_JOIN.ordinal());
        return set;
    }

    @Override
    public OptColumnRefSet getOutputColumns(OptExpression expression) {
        OptColumnRefSet columns = new OptColumnRefSet();
        for (int i = 0; i < 2; ++i) {
            columns.include(expression.getInput(i).getLogicalProperty().getOutputColumns());
        }
        return columns;
    }

    @Override
    public Statistics deriveStat(OptExpressionWapper wapper, StatisticsContext context) {
        final Statistics joinStatistics =
                new DefaultStatistics(RowCountProvider.getRowCount(wapper.getExpression(), context));
        return joinStatistics;
    }
}
