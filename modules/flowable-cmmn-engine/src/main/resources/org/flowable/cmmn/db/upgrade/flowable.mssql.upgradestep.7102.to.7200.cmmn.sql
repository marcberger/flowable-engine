ALTER TABLE ACT_CMMN_RU_PLAN_ITEM_INST ADD ASSIGNEE_ nvarchar(255);
ALTER TABLE ACT_CMMN_RU_PLAN_ITEM_INST ADD COMPLETED_BY_ nvarchar(255);

ALTER TABLE ACT_CMMN_HI_PLAN_ITEM_INST ADD ASSIGNEE_ nvarchar(255);
ALTER TABLE ACT_CMMN_HI_PLAN_ITEM_INST ADD COMPLETED_BY_ nvarchar(255);

update ACT_GE_PROPERTY set VALUE_ = '7.2.0.0' where NAME_ = 'cmmn.schema.version';