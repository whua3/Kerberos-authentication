# -Kerberos-authentication
java实现Keberos是第三方认证协议的认证过程

    在希腊神话中Kerberos是守护地狱之门的一条凶猛的三头神犬，而我们在本文中所要介绍的Kerberos认证协议是由美国麻省理工学院(MIT)首先提出并实现的，是该校雅典娜计划的一部分。Kerberos认证是一个三路处理过程，依赖称为密钥分发中心（KDC）的第三方服务来验证计算机相互的身份，并建立密钥以保证计算机间安全连接。其基本原理是在一个分布式的Client/Server体制机构中采用一个或多个Kerberos服务器提供一个鉴别服务。在一个客户端想请求应用服务器Server上的资源时，首先客户端向Kerberos认证服务器AS请求一个服务授权票据，然后将该票据交给票据授予服务器TGS进行验证，服务器在验证通过后，即为客户端分配一张身份证明（票据），然后将身份证明交给Server进行验证，Server在验证通过后，即为客户端分配请求资源。这种模式可使用户会话时仅需要询问一次口令。
    
    KDC是大家都信任的机构，其任务就是给需要进行秘密通信的用户临时分配一个会话密钥（仅使用一次），本质上每台计算机分享KDC一个秘钥，而KDC有两个部件：一个Kerberos 认证服务器和一个票据授权服务器。它允许在网络上通讯的实体互相证明彼此的身份，并且能够阻止窃听和重放等攻击手段。不仅如此，它还能够提供对通讯数据保密性和完整性的保护。
    
    本项目在学习及实现的kerberos认证系統的基础上，实现了多人聊天以及文件的传输等应用程序。
    本次项目，我们设计的是一个基于kerbeors认证的多用户聊天室，部署4台服务器，分别是AS、TGS、应用服务器（APPS），客户端多个。其中，APPS实现用户消息的存储与转发。
