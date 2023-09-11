import { UploadOutlined } from '@ant-design/icons';
import React, {useState} from 'react';
import {
  Button, Card, Col, Divider,
  Form, message, Row,
  Select,
  Space, Spin, Typography,
  Upload,
} from 'antd';

import TextArea from "antd/es/input/TextArea";
import Input from "antd/es/input/Input";
import {genChartUsingPOST} from "@/services/BI_Front/chartController";
import ReactECharts from 'echarts-for-react';
const AddChart: React.FC = () => {

  const [chart, setChart] =  useState<API.BiResponse>();
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [option, setOption] = useState<any>();
  const { Text } = Typography;

  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);
    setOption(undefined);
    setChart(undefined);
    const params = {
      ...values,
      file: undefined
    }
    try {
      const res = await  genChartUsingPOST(params, {}, values.file.file.originFileObj)
      if (!res.data) {
        message.error('分析失败,系统发生错误');
      } else {
        const chartOption = JSON.parse(res.data.genChart ?? '');
        if (!chartOption) {
          throw new Error('图表代码解析错误');
        } else {
          message.success('分析成功');
          setChart(res.data);
          setOption(chartOption);
        }
      }
    } catch (e: any) {
      message.error('分析失败：' + e.message);
    }
    setSubmitting(false);
  };

  return (
    <div className="add-chart">
      <Row gutter={24}>
        <Col span={12}>
          <Card title="智能分析（同步）">
            <Form name="addChartRequest"
                  onFinish={onFinish}
                  initialValues={{ }}
                  labelAlign="left"
                  labelCol={{span: 4}}
                  wrapperCol={{span: 16}}
            >
              <Form.Item name="goal" label="分析目标" rules={[{ required: true, message: '请输入分析目标！' }]}>
                <TextArea placeholder="请输入你的分析目标，例如：分析网站用户的增长情况"></TextArea>
              </Form.Item>

              <Form.Item name="name" label="图表名称">
                <Input placeholder="请输入图表名称"></Input>
              </Form.Item>

              <Form.Item
                name="chartType"
                label="图表类型"
              >
                <Select options={[
                  { value: '折线图', label: '折线图' },
                  { value: '柱状图', label: '柱状图' },
                  { value: '堆叠图', label: '堆叠图' },
                  { value: '饼图', label: '饼图' },
                  { value: '雷达图', label: '雷达图' },
                ]}>
                </Select>
              </Form.Item>

              <Form.Item
                name="file"
                label="原始数据"
              >
                <Upload name="file" maxCount={1} accept={'.xlsx, .xls, .doc, .docx, .txt'}>
                  <Button icon={<UploadOutlined />}>上传 Excel 文件</Button>
                </Upload>
              </Form.Item>

              <Form.Item wrapperCol={{ span: 12, offset: 4 }}>
                <Space>
                  <Button type="primary" htmlType="submit" loading={submitting} disabled={submitting}>
                    生成
                  </Button>
                  <Button htmlType="reset">重置</Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="分析结论">
            {
              chart?.genResult ?? <Text type="danger">请先在左侧填写好表单再点击生成！</Text>
            }
            <Spin spinning={submitting}></Spin>
          </Card>
          <Divider></Divider>
          <Card title="可视化图表">
            {
              option ? <ReactECharts option={option} /> : <Text type="danger">请先在左侧填写好表单再点击生成！</Text>
            }
            <Spin spinning={submitting}></Spin>
          </Card>
        </Col>
      </Row>
    </div>
  );
};
export default AddChart;
