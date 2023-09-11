import React, { useEffect, useState } from 'react';
import { listMyChartByPageUsingPOST } from "@/services/BI_Front/chartController";
import {Avatar, Card, List, message, Result} from 'antd';
import ReactECharts from "echarts-for-react";
import {useModel} from "@@/plugin-model/useModel";
import Search from "antd/es/input/Search";

const MyChartPage: React.FC = () => {

  const initSearchParams = {
    current: 1,
    pageSize: 6,
    sortField: 'createTime',
    sortOrder: 'desc',
  }

  const [ searchParams, setSearchParams ] = useState<API.ChartQueryRequest>({...initSearchParams});
  const [ chartList, setChartList ] = useState<API.Chart[]>();
  const [ total, setTotal ] = useState<number>(0);
  const [ loading, setLoading ] = useState<boolean>(true);
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await listMyChartByPageUsingPOST(searchParams);
      if (res.data) {
        //隐藏图表的title
        if (res.data.records) {
          res.data.records.forEach(data => {
            if (data.status === 'success') {
              const chartOption = JSON.parse(data.genChart ?? '{}');
              chartOption.title = undefined;
              data.genChart = JSON.stringify(chartOption);
            }
          })
        }
        setChartList(res.data.records ?? []);
        setTotal(res.data.total ?? 0);
      } else {
        message.error("获取我的图表失败");
      }

    } catch (e: any) {
      message.error("获取我的图表失败，" + e.message);
    }
    setLoading(false);
  }

  useEffect(() => {
    loadData();
  }, [searchParams]);


  return (
    <div className="my-chart-page">
      <div>
        <Search placeholder={'请输入图表名称'} enterButton loading={loading} onSearch={(value) => {
          setSearchParams({
            ...initSearchParams,
            name: value,
          })
        }}/>
      </div>
      <div className="margin-16"/>
      <List
        grid={{
          gutter: 16,
          xs: 1,
          sm: 1,
          md: 1,
          lg: 2,
          xl: 2,
          xxl: 2,
        }}
        pagination={{
          onChange: (page, pageSize) => {
            setSearchParams({
              ...searchParams,
              current: page,
              pageSize,
            })
          },
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total: total,
        }}
        loading={loading}
        dataSource={chartList}
        renderItem={(item) => (
          <List.Item key={item.id}>
            <Card>
              <List.Item.Meta
                avatar={<Avatar src={currentUser && currentUser.userAvatar} />}
                title={item.name}
                description={item.charType ? ('图表类型：' + item.charType) : undefined}
              />
              <>
                {
                  item.status === 'success' && <>
                    <div style={{marginBottom: 16}}/>
                    {'分析目标' + item.goal}
                    <div style={{marginBottom: 16}}/>
                    <ReactECharts option={JSON.parse(item.genChart ?? '{}')} />
                  </>
                }
                {
                  item.status === 'wait' && <>
                    <Result
                      status="info"
                      title="排队中......"
                      subTitle={item.execMessage ?? '当前生成请求较多，请耐心等候'} />
                  </>
                }
                {
                  item.status === 'running' && <>
                    <Result
                      status="info"
                      title="图表生成中......"
                      subTitle={item.execMessage ?? "AI正在为你生成图表..."} />
                  </>
                }
                {
                  item.status === 'failed' && <>
                    <Result
                      status="error"
                      title="图表生成失败"
                      subTitle={item.execMessage} />
                  </>
                }
              </>
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};
export default MyChartPage;
