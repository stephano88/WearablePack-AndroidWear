public class QuotePushTest {
    public static void createTestQuote() {
        PriceBookEntry pbe = [select Id, Pricebook2Id, Product2Id from PriceBookEntry][0];
        
        Opportunity opp = new Opportunity(Name='Test Opp',
                                          StageName='Proposal/Price Quote',
                                          CloseDate=System.Today());
        insert opp;
        
        Quote q = new Quote(OpportunityId=opp.Id,
                            Name='Test Quote',
                            Pricebook2Id=pbe.Pricebook2Id);
        insert q;
        
        QuoteLineItem qli = new QuoteLineItem(QuoteId=q.Id, 
                                              Product2Id=pbe.Product2Id, 
                                              Discount=30, 
                                              Quantity=1,
                                              PricebookEntryId=pbe.Id,
                                              UnitPrice=1000000);
        insert qli;
    }
}