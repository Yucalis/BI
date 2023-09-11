import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
const Footer: React.FC = () => {
  const defaultMessage = 'Yucalis 开发，仅学习参考使用！';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'Yucalis',
          title: 'Yucalis',
          href: '',
          blankTarget: false,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: '',
          blankTarget: false,
        },
        {
          key: '2406426905@qq.com',
          title: '2406426905@qq.com',
          href: '',
          blankTarget: false,
        },
      ]}
    />
  );
};
export default Footer;
